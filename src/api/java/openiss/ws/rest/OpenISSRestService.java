package openiss.ws.rest;

import openiss.Kinect;
import openiss.utils.PATCH;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;


import openiss.utils.OpenISSImageDriver;

@Path("/openiss")
public class OpenISSRestService {


    static String mixFlag = "default";
    static boolean cannyFlag = false;
    static boolean contourFlag = false;
    static OpenISSImageDriver driver;

    static {
        driver = new OpenISSImageDriver();
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Hello World from Jersey API!";
    }

    @GET
    @Path("/test")
    @Produces("image/*")
    public Response getTest() {
        byte[] image = driver.getFrame("depth");
        return Response.ok(pipelineImage(image)).build();
    }

    private static String colorFileName = "src/api/java/openiss/ws/soap/service/color_example.jpg";
    private static String depthFileName = "src/api/java/openiss/ws/soap/service/depth_example.jpg";

    @GET
    @Path("/{type}")
    @Produces("image/*")
    public Response getImage(@PathParam(value = "type") String type) {

        byte[] image = new byte[0];
        // validity checks
        if (!type.equals("rgb") && !type.equals("depth")) {
            return Response.noContent().build();
        }

        if(type.equals("rgb")) {
            image = driver.getFrame("color");
        } else {
            image = driver.getFrame("depth");
        }


        return Response.ok(pipelineImage(image)).build();
    }

    @PATCH
    @Path("/mix/{action}")
    @Produces("text/plain")
    /**
     * the GET images will be mixed with depth, rgb or canny
     */
    public String enableMix(@PathParam(value = "action") String action) {
        if(action.equals("depth") || action.equals("rgb") || action.equals("canny")) {
            mixFlag = action;
        }
        return getFlags();
    }


    @DELETE
    @Path("/mix")
    @Produces("text/plain")
    public String disableMix() {
        mixFlag = "default";
        return getFlags();
    }

    @PATCH
    @Path("/opencv/{type}")
    @Produces("text/plain")
    public String enableOpenCV(@PathParam(value = "type") String type) {

        // validity checks
        if (!type.equals("canny") && !type.equals("contour")) {
            return "Service not supported";
        }

        if (type.equals("canny")) {
            cannyFlag = true;
        } else if(type.equals("contour")) {
            contourFlag = true;
        }
        return getFlags();
    }


    @DELETE
    @Path("/opencv/{type}")
    @Produces("text/plain")
    public String disableOpenCV(@PathParam(value = "type") String type) {

        // validity checksX
        if (!type.equals("canny") && !type.equals("contour")) {
            return "Service not supported.";
        }
        if (type.equals("canny")) {
            cannyFlag = false;
        } else if(type.equals("contour")) {
            contourFlag = false;
        }
        return getFlags();
    }

    private String getFlags() {
        String flags = "Mix: " + String.valueOf(mixFlag) +
                "\nCanny: " + String.valueOf(cannyFlag) +
                "\nContour: " + String.valueOf(contourFlag);
        return flags;
    }

    private byte[] pipelineImage(byte[] image) {

        byte[] processedImage = image;

        if(mixFlag.equals("depth")) {
            processedImage = driver.mixFrame(image, "depth", "+");
        } else if(mixFlag.equals("rgb")) {
            processedImage = driver.mixFrame(image, "color", "+");
        } else if(mixFlag.equals("canny")) {
            // todo: add docanny support
            // mix with do canny
        }


        if(cannyFlag) {
            // todo
            // put image through canny        	processedImage = driver.doCanny(image);
        }

        if(contourFlag) {
            // todo
            // put image through
        }

        return processedImage;



    }


}
