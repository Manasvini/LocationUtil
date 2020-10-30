package com.epl.annotationmanager;

import android.os.AsyncTask;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import epl.location.locationmanager.AddAnnotationsRequest;
import epl.location.locationmanager.AddAnnotationsResponse;

import epl.location.locationmanager.Annotation;
import epl.location.locationmanager.GetAnnotationsRequest;
import epl.location.locationmanager.GetAnnotationsResponse;
import epl.location.locationmanager.LocationManagerGrpc;
import epl.location.locationmanager.Point;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


import epl.location.locationmanager.LocationManagerGrpc.LocationManagerBlockingStub;


public class GrpcClient extends AsyncTask<Void, Void, String> {

    String TAG = "GrpcClient";
    private interface GrpcRunnable {
        /** Perform a grpcRunnable and return all the logs. */
        String run(LocationManagerBlockingStub blockingStub) throws Exception;
    };

    private  GrpcRunnable grpcRunnable;
    private final ManagedChannel mChannel;

    private RequestCompleteCallback mCb;
    String mHost ="";
    int mPort = 0;

    public GrpcClient(String host, int port, RequestCompleteCallback cb){
        mHost = host;
        mPort = port;
        mCb = cb;
        mChannel = ManagedChannelBuilder.forAddress(mHost, mPort).usePlaintext().build();

    }
    public void getAnnotatedRoute(List<LatLong> latLngs, List<String> annotationNames){
        grpcRunnable = new GetAnnotationsRunnable(latLngs, annotationNames);
        execute();
    }

    public void addAnnotations(List<AnnotationObject> annotations){
        grpcRunnable = new AddAnnotationsRunnable(annotations);
        execute();
    }

    private class GetAnnotationsRunnable implements  GrpcRunnable{
        private List<LatLong> mLatLngs;
        private  List<String> mAnnotationNames;
        public GetAnnotationsRunnable(List<LatLong> latLngs, List<String> annotationNames){
            mLatLngs = latLngs;
            mAnnotationNames = annotationNames;
        }
        @Override
        public String run(LocationManagerBlockingStub blockingStub) throws Exception{
            try{
                GetAnnotationsRequest request;

                GetAnnotationsRequest.Builder builder = GetAnnotationsRequest.newBuilder();
                for(LatLong l: mLatLngs){
                    Point p;
                    Point.Builder pb = Point.newBuilder();
                    pb.setLatitude(l.getLatitude());
                    pb.setLongitude(l.getLongitude());
                    builder.addLocations(pb.build());
                }
                builder.addAllAnnotationtypes(mAnnotationNames);
                request = builder.build();
                List<AnnotationObject> annotations = new ArrayList<>();

                Iterator<GetAnnotationsResponse> responses = blockingStub.getAnnotations(request);
                for (Iterator<GetAnnotationsResponse> it = responses; it.hasNext(); ) {
                    GetAnnotationsResponse response = it.next();

                    for(Annotation ann: response.getAnnotationsList()){
                        AnnotationObject annotationObject = new AnnotationObject();
                        annotationObject.setAnnotationName(ann.getName());
                        annotationObject.setAnnotationValue(ann.getValue());
                        annotationObject.setAnnotationValueType(ann.getType());
                        annotationObject.setAnnotationType(ann.getAnnotationtype());
                        LatLong latLong  = new LatLong();
                        latLong.setLatitude(ann.getLocation().getLatitude());
                        latLong.setLongitude(ann.getLocation().getLongitude());
                        annotationObject.setLatLong(latLong);
                        annotations.add(annotationObject);
                    }

                }
                AnnotationObjectList annotationObjectList = new AnnotationObjectList();
                annotationObjectList.setAnnotationObjectList(annotations);
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(annotationObjectList);


                return json;
            }
            catch(StatusRuntimeException ex){
                Log.i(TAG, ex.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                pw.flush();
            }
            return "FAIL";
        }

    }
    private class AddAnnotationsRunnable implements GrpcRunnable{
        private List<AnnotationObject> mAnnotations;
        public AddAnnotationsRunnable(List<AnnotationObject> annotations){
            mAnnotations = annotations;
        }
        @Override
        public String run(LocationManagerBlockingStub blockingStub) throws Exception{
            AddAnnotationsRequest request;
            AddAnnotationsRequest.Builder builder = AddAnnotationsRequest.newBuilder();
            List<Annotation> annotations = new ArrayList<>();
            for(AnnotationObject annotationObject: mAnnotations){
                Annotation annotation;
                Annotation.Builder aBuilder = Annotation.newBuilder();
                aBuilder.setAnnotationtype(annotationObject.getAnnotationType());
                aBuilder.setName(annotationObject.getAnnotationName());
                aBuilder.setType(annotationObject.getAnnotationValueType());
                aBuilder.setValue(annotationObject.getAnnotationValue());
                Point.Builder pb = Point.newBuilder();
                pb.setLatitude(annotationObject.getLatLong().getLatitude());
                pb.setLongitude(annotationObject.getLatLong().getLongitude());
                aBuilder.setLocation(pb.build());
                annotation = aBuilder.build();
                annotations.add(annotation);
            }
            builder.addAllAnnotations(annotations);
            request = builder.build();
            AddAnnotationsResponse response =  blockingStub.addAnnotations(request);
            return  response.getStatus();
        }
    }
    @Override
    protected  String doInBackground(Void... nothing){
        try{
            Log.i(TAG, "sending req");
            String result = grpcRunnable.run(LocationManagerGrpc.newBlockingStub(mChannel));
            mCb.onRequestComplete(result);
            return "SUCCESS";
        }
        catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();

        }
        return "FAIL";
    }

    @Override
    protected void onPostExecute(String result){
        Log.i(TAG, "status is" + result);
    }
}
