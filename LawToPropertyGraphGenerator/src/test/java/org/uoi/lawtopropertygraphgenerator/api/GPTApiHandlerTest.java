package org.uoi.lawtopropertygraphgenerator.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uoi.lawtopropertygraphgenerator.model.law.Entity;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GPTApiHandlerTest {

    private GPTApiHandler handler;

    @BeforeEach
    void setUp() {
        handler = Mockito.spy(new GPTApiHandler());
    }

    @Test
    void testSetupEntitiesAndGiveExample_happyDay() {
        doReturn(List.of(new Entity("AI system", "An artificial intelligence system"))).when(handler).readEntitiesFile();

        Response mockResponse = mock(Response.class);
        when(mockResponse.body()).thenReturn(mock(io.restassured.response.ResponseBody.class));
        when(mockResponse.body().jsonPath()).thenReturn(mock(io.restassured.path.json.JsonPath.class));
        when(mockResponse.body().jsonPath().getString("id")).thenReturn("mock-context-id");

        doReturn(mockResponse).when(handler).sendHttpPostRequest(any(JSONObject.class));
        
        handler.setupEntitiesAndGiveExample();
        verify(handler, times(1)).readEntitiesFile();
        verify(handler, atLeastOnce()).sendHttpPostRequest(any(JSONObject.class));
    }

    @Test
    void testPassInstructionsToModel_happyDay() {
        Response mockResponse = mock(Response.class);
        when(mockResponse.body()).thenReturn(mock(io.restassured.response.ResponseBody.class));
        when(mockResponse.body().jsonPath()).thenReturn(mock(io.restassured.path.json.JsonPath.class));

        doReturn(mockResponse).when(handler).sendHttpPostRequest(any(JSONObject.class));

        handler.passInstructionsToModel();

        verify(handler, atLeastOnce()).sendHttpPostRequest(any(JSONObject.class));
    }

    @Test
    void testExtractRelationships_happyDay() {
        Response mockResponse = mock(Response.class);
        ResponseBody mockBody = mock(ResponseBody.class);

        JsonPath mockJsonPath = mock(JsonPath.class);

        when(mockResponse.getBody()).thenReturn(mockBody);
        when(mockBody.jsonPath()).thenReturn(mockJsonPath);
        when(mockJsonPath.getString("choices[0].message.content")).thenReturn("<graphML>Mock GraphML Content</graphML>");

        doReturn(mockResponse).when(handler).sendHttpPostRequest(any(JSONObject.class));

        handler.extractRelationships("Sample paragraph text", "P1");

        verify(handler, atLeastOnce()).sendHttpPostRequest(any(JSONObject.class));
    }
}
