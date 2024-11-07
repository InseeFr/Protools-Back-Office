//package fr.insee.protools.backend.restclient;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.JsonNodeType;
//import fr.insee.protools.backend.restclient.configuration.APIProperties;
//import fr.insee.protools.backend.restclient.configuration.ApiConfigProperties;
//import fr.insee.protools.backend.restclient.exception.ApiNotConfiguredBPMNError;
//import fr.insee.protools.backend.restclient.exception.KeycloakTokenConfigUncheckedBPMNError;
//import fr.insee.protools.backend.restclient.exception.runtime.HttpClient4xxBPMNError;
//import fr.insee.protools.backend.restclient.keycloak.KeycloakResponse;
//import fr.insee.protools.backend.restclient.keycloak.KeycloakService;
//import fr.insee.protools.backend.service.utils.CustomJWTHelper;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.web.client.RestClient;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class RestClientHelperTest {
//
//    @Spy
//    private Environment environment;
//
//    private KeycloakService keycloakService;
//
//    @Mock
//    private ApiConfigProperties apiConfigProperties;
//
//    private RestClientHelper restClientHelper;
//
//
//    private MockWebServer mockWebServer;
//    private static final int port = 80;
//    private static String getDummyUriWithPort() { return getServerHostPort()+"/api/test"; }
//    private static String getServerHostPort() { return "http://localhost:"+port; }
//
//    @BeforeEach
//    public void prepare() {
//        keycloakService= spy(new KeycloakService(environment));
//        restClientHelper = new RestClientHelper(keycloakService,apiConfigProperties);
//        this.keycloakService.initialize();
//    }
//
//    //close the mocked web server if it has been initialized
//    @AfterEach
//    void mockServerCleanup() throws Exception {
//        if(this.mockWebServer!=null){
//            this.mockWebServer.close();
//        }
//    }
//
//    private void initMockWebServer() throws IOException {
//        this.mockWebServer = new MockWebServer();
//        mockWebServer.start(port);
//    }
//
//    @Test
//    @DisplayName("Test getRestClient method without specifying an API")
//    void testgetRestClientWithoutApi() {
//        RestClient client = restClientHelper.getRestClient();
//        assertThat(client).isNotNull();
//    }
//
//    private void testgetRestClientWithIncompleteKCConfig(APIProperties.AuthProperties kcAuth){
//        when(apiConfigProperties.getAPIProperties(any())).thenReturn(new APIProperties(getServerHostPort(), kcAuth, true));
//        var client = restClientHelper.getRestClient(ApiConfigProperties.KNOWN_API.KNOWN_API_ERA);
//        assertThat(client).isNotNull();
//        //Should throw an exception as the realm is missing
//        assertThrows(KeycloakTokenConfigUncheckedBPMNError.class , ()  -> client.get().uri(getDummyUriWithPort()).retrieve());
//    }
//    @Test
//    @DisplayName("Test getRestClient method without incomplete keycloak configuration")
//    void getRestClientWithIncompleteKCConfig() throws IOException {
//        //Missing realm
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties(getDummyUriWithPort(),null, "toto","toto");
//        //Should throw an exception as the realm is missing
//        testgetRestClientWithIncompleteKCConfig(kcAuth);
//
//        //Missing url
//        kcAuth = new APIProperties.AuthProperties("null","realm", "toto","toto");
//        testgetRestClientWithIncompleteKCConfig(kcAuth);
//
//        //Missing clientId
//        kcAuth = new APIProperties.AuthProperties(getDummyUriWithPort(),"realm", null,"toto");
//        testgetRestClientWithIncompleteKCConfig(kcAuth);
//
//        //Missing secret
//        kcAuth = new APIProperties.AuthProperties(getDummyUriWithPort(),"realm", "toto",null);
//        testgetRestClientWithIncompleteKCConfig(kcAuth);
//    }
//
//    @Test
//    void getRestClient_should_work_when_OK() throws IOException {
//
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties();
//        kcAuth.setClientId("clientId-toto");
//        kcAuth.setClientSecret("client-pwd-toto");
//        kcAuth.setRealm("realm-toto");
//        kcAuth.setUrl(getDummyUriWithPort());
//
//
//        when(apiConfigProperties.getAPIProperties(any())).thenReturn(new APIProperties(getServerHostPort(), kcAuth, true));
//        RestClient client = restClientHelper.getRestClient(ApiConfigProperties.KNOWN_API.KNOWN_API_ERA);
//        assertThat(client).isNotNull();
//
//        KeycloakResponse kcResponse = new KeycloakResponse("MYTOKEN",5*60*1000);
//        MockResponse mockResponseKC = new MockResponse()
//                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                .setResponseCode(HttpStatus.OK.value())
//                .setBody(new ObjectMapper().writeValueAsString(kcResponse));
//
//        MockResponse mockResponse = new MockResponse()
//                .setResponseCode(HttpStatus.NO_CONTENT.value())
//                .setBody("XXX");
//
//        initMockWebServer();
//        mockWebServer.enqueue(mockResponseKC);
//        mockWebServer.enqueue(mockResponse);
//
//        assertThat(client.get().uri(getDummyUriWithPort()).retrieve().toBodilessEntity().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//    }
//
//    @Test
//    void getRestClient_withInvalidApiConfig() {
//        when(apiConfigProperties.getAPIProperties(any())).thenReturn(null);
//        assertThatThrownBy(() -> restClientHelper.getRestClient(any()))
//                .isInstanceOf(ApiNotConfiguredBPMNError.class)
//                .hasMessageContaining("is not configured in properties");
//    }
//
//    @Test
//    void getRestClient_withDisabledApiConfig() {
//        when(apiConfigProperties.getAPIProperties(any())).thenReturn(new APIProperties("http://localhost:8080", new APIProperties.AuthProperties(), false ));
//        assertThatThrownBy(() -> restClientHelper.getRestClient(any()))
//                .isInstanceOf(ApiNotConfiguredBPMNError.class)
//                .hasMessageContaining("is disabled in properties");
//    }
//
//    @Test
//    void getTokenDetailsByAPI_should_workForOkNullAndNotConfiguredApi() throws Exception {
//        //Prepare :
//        // Platine pilotage is disabled
//        // Meshuggah is null
//        // All other are confired and returns the same token
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties("https://test.test","realm", "client","xxx");
//        APIProperties apiPropertiesAll = new APIProperties("url-all",kcAuth ,true);
//        APIProperties apiPropertiesPlatinePil = new APIProperties("url-platine",kcAuth ,false);
//
//        doReturn(apiPropertiesAll).when(apiConfigProperties).getAPIProperties(any());
//        doReturn(apiPropertiesPlatinePil).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE));
//        doReturn(null).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_MESHUGGAH));
//
//        doReturn(CustomJWTHelper.getEncodedToken(List.of("ROLE_TOTO","ROLE_Administrateurs_BEATLES"))).when(keycloakService).getToken(eq(kcAuth));
//
//
//        Map<String,String>  details = restClientHelper.getTokenDetailsByAPI();
//
//        assertThat(details).containsEntry("KNOWN_API_PLATINE_PILOTAGE","API KNOWN_API_PLATINE_PILOTAGE is disabled in properties");
//        assertThat(details).containsEntry("KNOWN_API_MESHUGGAH","API KNOWN_API_MESHUGGAH is not configured in properties");
//        assertThat(details).containsEntry("KNOWN_API_REM","[\"ROLE_TOTO\",\"ROLE_Administrateurs_BEATLES\"]");
//
//    }
//
//    @Test
//    void getTokenDetailsByAPI_should_workWhenNoRoleInToken() throws Exception {
//        //Prepare :
//        // Platine pilotage is disabled
//        // Meshuggah is null
//        // All other are confired and returns the same token
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties("https://test.test","realm", "client","xxx");
//        APIProperties apiPropertiesAll = new APIProperties("url-all",kcAuth ,true);
//        APIProperties apiPropertiesPlatinePil = new APIProperties("url-platine",kcAuth ,false);
//
//        doReturn(apiPropertiesAll).when(apiConfigProperties).getAPIProperties(any());
//        doReturn(apiPropertiesPlatinePil).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE));
//        doReturn(null).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_MESHUGGAH));
//
//        doReturn(CustomJWTHelper.getEncodedToken(List.of())).when(keycloakService).getToken(eq(kcAuth));
//
//
//        Map<String,String>  details = restClientHelper.getTokenDetailsByAPI();
//
//        assertThat(details).containsEntry("KNOWN_API_PLATINE_PILOTAGE","API KNOWN_API_PLATINE_PILOTAGE is disabled in properties");
//        assertThat(details).containsEntry("KNOWN_API_MESHUGGAH","API KNOWN_API_MESHUGGAH is not configured in properties");
//        assertThat(details).containsEntry("KNOWN_API_REM","No Role found in token");
//
//    }
//
//
//    @Test
//    void getTokenDetailsByAPI_should_workWhenNoDot() throws Exception {
//        //Prepare :
//        // Platine pilotage is disabled
//        // Meshuggah is null
//        // All other are confired and returns the same token
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties("https://test.test","realm", "client","xxx");
//        APIProperties apiPropertiesAll = new APIProperties("url-all",kcAuth ,true);
//        APIProperties apiPropertiesPlatinePil = new APIProperties("url-platine",kcAuth ,false);
//
//        doReturn(apiPropertiesAll).when(apiConfigProperties).getAPIProperties(any());
//        doReturn(apiPropertiesPlatinePil).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE));
//        doReturn(null).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_MESHUGGAH));
//
//        doReturn("{THIS TOKEN IS INCORRECT AND THERE IS NO DOT SEPARING HEADER}}}}").when(keycloakService).getToken(eq(kcAuth));
//
//
//        Map<String,String>  details = restClientHelper.getTokenDetailsByAPI();
//
//        assertThat(details).containsEntry("KNOWN_API_PLATINE_PILOTAGE","API KNOWN_API_PLATINE_PILOTAGE is disabled in properties");
//        assertThat(details).containsEntry("KNOWN_API_MESHUGGAH","API KNOWN_API_MESHUGGAH is not configured in properties");
//        assertThat(details).containsEntry("KNOWN_API_REM","Token size is incorrect. It should contain at least one dot");
//
//    }
//
//
//    @Test
//    void getTokenDetailsByAPI_should_workWhenIncorrectJsonContent() throws Exception {
//        //Prepare :
//        // Platine pilotage is disabled
//        // Meshuggah is null
//        // All other are confired and returns the same token
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties("https://test.test","realm", "client","xxx");
//        APIProperties apiPropertiesAll = new APIProperties("url-all",kcAuth ,true);
//        APIProperties apiPropertiesPlatinePil = new APIProperties("url-platine",kcAuth ,false);
//
//        doReturn(apiPropertiesAll).when(apiConfigProperties).getAPIProperties(any());
//        doReturn(apiPropertiesPlatinePil).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE));
//        doReturn(null).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_MESHUGGAH));
//
//        String header= Base64.getEncoder().encodeToString("random header".getBytes(StandardCharsets.UTF_8));
//        String incorrectPayload= Base64.getEncoder().encodeToString("incorrect json".getBytes(StandardCharsets.UTF_8));
//        String signature=CustomJWTHelper.createSignature(header, incorrectPayload);
//        String signedToken = header + "." + incorrectPayload + "." + signature;
//
//        doReturn(signedToken).when(keycloakService).getToken(eq(kcAuth));
//
//
//        Map<String,String>  details = restClientHelper.getTokenDetailsByAPI();
//
//        assertThat(details).containsEntry("KNOWN_API_PLATINE_PILOTAGE","API KNOWN_API_PLATINE_PILOTAGE is disabled in properties");
//        assertThat(details).containsEntry("KNOWN_API_MESHUGGAH","API KNOWN_API_MESHUGGAH is not configured in properties");
//        assertThat(details).containsEntry("KNOWN_API_REM","Exception during json token parsing");
//
//    }
//
//
//
//    @Test
//    void getAPIConfigDetails_shouldNotThrow() {
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties("https://test.test","realm", "client","xxx");
//        APIProperties apiPropertiesAll = new APIProperties("url-all",kcAuth ,true);
//        doReturn(apiPropertiesAll).when(apiConfigProperties).getAPIProperties(any());
//
//        JsonNode result = restClientHelper.getAPIConfigDetails();
//        assertEquals(JsonNodeType.ARRAY,result.getNodeType());
//        ArrayNode resultArray = (ArrayNode) result;
//        assertEquals(ApiConfigProperties.KNOWN_API.values().length,resultArray.size());
//    }
//
//    @Test
//    @DisplayName("Test that the retrieval of spring private field still works")
//    void extractClientResponseRequestDescriptionPrivateFiledUsingReflexion_shouldWork() throws IOException {
//        RestClient restClient = restClientHelper.getRestClient();
//        assertThat(restClient).isNotNull();
//
//        //Mock an error response
//        MockResponse mockResponse = new MockResponse()
//                .setResponseCode(HttpStatus.BAD_REQUEST.value())
//                .setBody("XXX");
//
//        initMockWebServer();
//        mockWebServer.enqueue(mockResponse);
//
//        //Call method under test
//        HttpClient4xxBPMNError exception = assertThrows(HttpClient4xxBPMNError.class , ()  ->restClient.get().uri(getDummyUriWithPort()).retrieve()
//                .body(String.class));
//
//        //Post call conditions (we get more or less the expected message with the original request)
//        //IF it is not the case, check that the spring private field has not changed or been renamed
//        String actualMessage = exception.getMessage();
//        assertThat(actualMessage)
//                .contains("GET")
//                .contains(getDummyUriWithPort())
//                .contains(String.valueOf(HttpStatus.BAD_REQUEST.value()));
//    }
//
//    @Test
//    @DisplayName("Test that containsCauseOfType find an existing cause")
//    void containsCauseOfType_shouldFindCauseIfExists() {
//        //Prepare
//        String rootMessage="TEST";
//        ArithmeticException exRoot=new ArithmeticException(rootMessage);
//        Exception exLvl1=new Exception("dummy",exRoot );
//        Exception ex = new Exception("dummy",exLvl1 );
//
//        //Call
//        boolean found = RestClientHelper.containsCauseOfType(ex, List.of(ArithmeticException.class));
//        //Check
//        assertTrue(found,"ArithmeticException should be found");
//
//        //Call
//        found = RestClientHelper.containsCauseOfType(exLvl1, List.of(ArithmeticException.class));
//        //Check
//        assertTrue(found,"ArithmeticException should be found");
//
//        //Call
//        found = RestClientHelper.containsCauseOfType(exRoot, List.of(ArithmeticException.class));
//        //Check
//        assertTrue(found,"ArithmeticException should be found");
//
//        //Call
//        found = RestClientHelper.containsCauseOfType(exRoot, List.of(RuntimeException.class));
//        //Check
//        assertTrue(found,"RuntimeException should be found");
//
//        //Call
//        found = RestClientHelper.containsCauseOfType(exRoot, List.of(IOException.class,RuntimeException.class));
//        //Check
//        assertTrue(found,"RuntimeException should be found");
//
//        //Call
//        found = RestClientHelper.containsCauseOfType(exRoot, List.of(IOException.class));
//        //Check (should not be found)
//        assertFalse(found,"IOException should not be found");
//    }
//
//    /*
//
//
//    private MockResponse fileToResponse(String contentType, File file) throws IOException {
//        return new MockResponse()
//                .setResponseCode(HttpStatus.OK.value())
//                .setBody(ProtoolsTestUtils.fileToBytes(file))
//                .addHeader("content-type: " + contentType);
//    }
//
//    private File createDummyFile(int sizeInByte, String extension) throws IOException {
//        File file = File.createTempFile("tempFile", ".json");
//        file.deleteOnExit();
//        RandomAccessFile rafile;
//        rafile = new RandomAccessFile(file, "rw");
//        //In Bytes ==> 1024 = 1Ko ==> 1024X1024 : 1Mo
//        rafile.setLength(sizeInByte);
//        return file;
//    }
//
//    @Test
//    @DisplayName("Test getRestClientForFile method - get the client and download files of different sizes")
//    void getRestClientForFile() throws IOException {
//        RestClient client = restClientHelper.getRestClient();
//        assertThat(client).isNotNull();
//
//        //Create a 1Mo File
//        File file1mo = createDummyFile(1024 * 1024 * 1,".json");
//        File file19mo = createDummyFile(RestClientHelper.getDefaultFileBufferSize()-1024,".json");
//        int tooBigSize=RestClientHelper.getDefaultFileBufferSize()+1024;
//        File fileTooBig = createDummyFile(tooBigSize,".json");
//
//        MockResponse fileToResponse_1Mo = fileToResponse(MediaType.APPLICATION_JSON_VALUE, file1mo);
//        MockResponse fileToResponse_19Mo = fileToResponse(MediaType.APPLICATION_JSON_VALUE, file19mo);
//        MockResponse fileToResponseTooBig = fileToResponse(MediaType.APPLICATION_JSON_VALUE, fileTooBig);
//
//
//
//
//      KeycloakResponse kcResponse = new KeycloakResponse();
//        kcResponse.setAccesToken("MYTOKEN");
//        kcResponse.setExpiresIn(5*60*1000);
//
//
//        MockResponse mockResponseKC = new MockResponse()
//                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                .setResponseCode(HttpStatus.OK.value())
//                .setBody(new ObjectMapper().writeValueAsString(kcResponse));
//
//        final Dispatcher dispatcher = new Dispatcher() {
//            public MockResponse dispatch(RecordedRequest request) {
//                switch (request.getPath()) {
//                    case "/users/1":
//                        return new MockResponse().setResponseCode(200);
//                    case "/users/2":
//                        return new MockResponse().setResponseCode(500);
//                    case "/users/3":
//                        return new MockResponse().setResponseCode(200).setBody("{\"id\": 1, \"name\":\"duke\"}");
//                }
//                return new MockResponse().setResponseCode(404);
//            }
//
//                    mockWebServer.enqueue(mockResponseKC);
//
//
//
//        initMockWebServer();
//        mockWebServer.enqueue(fileToResponse_1Mo);
//        mockWebServer.enqueue(fileToResponse_19Mo);
//        mockWebServer.enqueue(fileToResponseTooBig);
//        mockWebServer.enqueue(fileToResponseTooBig);
//
//        //Check for 1Mo file
//        assertDoesNotThrow(()-> restClientHelper.getRestClientForFile()
//                .get()
//                .uri(getDummyUriWithPort())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block());
//
//        //Check for 19Mo file
//        assertDoesNotThrow(()-> restClientHelper.getRestClientForFile()
//                .get()
//                .uri(getDummyUriWithPort())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block());
//
//        //Check for a too big file : Should throw and exception as default buffer is 20Mo
//        assertThrows(clientResponseException.class, () -> restClientHelper.getRestClientForFile()
//                .get()
//                .uri(getDummyUriWithPort())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block());
//
//        //Check for a too big file with a custom buffer ==> Should be ok
//        assertDoesNotThrow(() -> restClientHelper.getRestClientForFile(tooBigSize)
//                .get()
//                .uri(getDummyUriWithPort())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block());
//    }
//
//        };*/
//
//}