package com.maxmind.geoip2.webservice;

import java.io.IOException;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

public final class TestTransport extends MockHttpTransport {
    private final static String omniBody = "{" + "\"city\":{"
            + "\"confidence\":76," + "\"geoname_id\":9876," + "\"names\":{"
            + "\"en\":\"Minneapolis\"" + "}" + "}," + "\"continent\":{"
            + "\"continent_code\":\"NA\"," + "\"geoname_id\":42,"
            + "\"names\":{" + "\"en\":\"North America\"" + "}" + "},"
            + "\"country\":{" + "\"confidence\":99," + "\"iso_code\":\"US\","
            + "\"geoname_id\":1," + "\"names\":{"
            + "\"en\":\"United States of America\"" + "}" + "},"
            + "\"location\":{" + "\"accuracy_radius\":1500,"
            + "\"latitude\":44.98," + "\"longitude\":93.2636,"
            + "\"metro_code\":765," + "\"time_zone\":\"America/Chicago\""
            + "}," + "\"postal\":{\"confidence\": 33, \"code\":\"55401\"},"
            + "\"registered_country\":{" + "\"geoname_id\":2,"
            + "\"iso_code\":\"CA\"," + "\"names\":{" + "\"en\":\"Canada\""
            + "}" + "}," + "\"represented_country\":{" + "\"geoname_id\":3,"
            + "\"iso_code\":\"GB\"," + "\"names\":{"
            + "\"en\":\"United Kingdom\"" + "}," + "\"type\":\"C<military>\""
            + "}," + "\"subdivisions\":[{" + "\"confidence\":88,"
            + "\"geoname_id\":574635," + "\"iso_code\":\"MN\"," + "\"names\":{"
            + "\"en\":\"Minnesota\"" + "}" + "}," + "{\"iso_code\":\"TT\"}],"
            + "\"traits\":{" + "\"autonomous_system_number\":1234,"
            + "\"autonomous_system_organization\":\"AS Organization\","
            + "\"domain\":\"example.com\"," + "\"ip_address\":\"1.2.3.4\","
            + "\"is_anonymous_proxy\":true,"
            + "\"is_satellite_provider\":true," + "\"isp\":\"Comcast\","
            + "\"organization\":\"Blorg\"," + "\"user_type\":\"college\""
            + "}," + "\"maxmind\":{\"queries_remaining\":11}" + "}";

    private final static String namesBody = "{\"continent\":{"
            + "\"continent_code\":\"NA\"," + "\"geoname_id\":42,"
            + "\"names\":{" + "\"en\":\"North America\"," + "\"zh-CN\":\"北美洲\""
            + "}" + "}," + "\"country\":{" + "\"geoname_id\":1,"
            + "\"iso_code\":\"US\"," + "\"confidence\":56," + "\"names\":{"
            + "\"en\":\"United States\","
            + "\"ru\":\"объединяет государства\"," + "\"zh-CN\":\"美国\"" + "}"
            + "}," + "\"registered_country\":{" + "\"geoname_id\":2,"
            + "\"iso_code\":\"CA\"," + "\"names\":{\"en\":\"Canada\"}" + "},"
            + "\"traits\":{" + "\"ip_address\":\"1.2.3.4\"" + "}}";

    private final static String clientBody = "{\"continent\":{"
            + "\"continent_code\":\"NA\"," + "\"geoname_id\":42,"
            + "\"names\":{\"en\":\"North America\"}" + "}," + "\"country\":{"
            + "\"geoname_id\":1," + "\"iso_code\":\"US\","
            + "\"confidence\":56," + "\"names\":{\"en\":\"United States\"}"
            + "}," + "\"registered_country\":{" + "\"geoname_id\":2,"
            + "\"iso_code\":\"CA\"," + "\"names\":{\"en\":\"Canada\"}" + "},"
            + "\"traits\":{" + "\"ip_address\":\"1.2.3.4\"" + "}}";

    private final static String countryBody = "{\"continent\":{"
            + "\"continent_code\":\"NA\"," + "\"geoname_id\":42,"
            + "\"names\":{\"en\":\"North America\"}" + "}," + "\"country\":{"
            + "\"geoname_id\":1," + "\"iso_code\":\"US\","
            + "\"confidence\":56," + "\"names\":{\"en\":\"United States\"}"
            + "}," + "\"registered_country\":{" + "\"geoname_id\":2,"
            + "\"iso_code\":\"CA\"," + "\"names\":{\"en\":\"Canada\"}},"
            + "\"represented_country\":{" + "\"geoname_id\":4,"
            + "\"iso_code\":\"GB\"," + "\"names\":{\"en\":\"United Kingdom\"},"
            + "\"type\":\"military\"}," + "\"traits\":{"
            + "\"ip_address\":\"1.2.3.4\"" + "}}";

    @Override
    public LowLevelHttpRequest buildRequest(String method, final String url)
            throws IOException {
        return new MockLowLevelHttpRequest() {

            @Override
            public LowLevelHttpResponse execute() throws IOException {
                String path = url.replaceFirst(
                        "https://geoip.maxmind.com/geoip/v2.0/", "");
                // only 1.7 supports switching on strings
                // XXX - This could probably be cleaned up by dispatching to
                // separate
                // methods for the different endpoints
                if (path.equals("omni/1.1.1.1")) {
                    return this.getResponse("country", 200,
                            TestTransport.omniBody);
                } else if (path.equals("city_isp_org/1.1.1.2")) {
                    return this.getResponse("country", 200,
                            TestTransport.namesBody);
                } else if (path.equals("country/1.1.1.3")) {
                    return this.getResponse("country", 200,
                            TestTransport.countryBody);
                } else if (path.equals("country/1.2.3.4")) {
                    return this.getResponse("country", 200, clientBody);
                } else if (path.equals("country/1.2.3.5")) {
                    return this.getResponse("country", 200);
                } else if (path.equals("country/1.2.3.6")) {
                    String body = "{\"code\":\"IP_ADDRESS_INVALID\","
                            + "\"error\":\"The value 1.2.3 is not a valid ip address\"}";
                    return this.getResponse("error", 400, body);
                } else if (path.equals("country/1.2.3.7")) {
                    return this.getResponse("error", 400);
                } else if (path.equals("country/1.2.3.8")) {
                    return this.getResponse("error", 400, "{\"weird\":42}");
                } else if (path.equals("country/1.2.3.9")) {
                    return this.getResponse("error", 400, "{ invalid: }");
                } else if (path.equals("country/1.2.3.10")) {
                    return this.getResponse("", 500);
                } else if (path.equals("country/1.2.3.11")) {
                    return this.getResponse("", 300);
                } else if (path.equals("country/1.2.3.12")) {
                    return this.getResponse("error", 406,
                            "Cannot satisfy your Accept-Charset requirements",
                            "text/plain");
                } else if (path.equals("omni/1.2.3.13")) {
                    return this.getResponse("omni", 200, "{}");
                } else if (path.equals("omni/1.2.3.14")) {
                    return this.getResponse("omni", 200, clientBody,
                            "bad/content-type");
                } else if (path.equals("city_isp_org/1.2.3.15")) {
                    return this.getResponse("omni", 200, "{\"invalid\":yes}");
                } else if (path
                        .equals("https://blah.com/geoip/v2.0/omni/128.101.101.101")) {
                    return this
                            .getResponse("omni", 200,
                                    "{\"traits\":{\"ip_address\": \"128.101.101.101\"}}");
                } else if (path.endsWith("me")) {
                    return this.getResponse("omni", 200,
                            "{\"traits\":{\"ip_address\": \"24.24.24.24\"}}");
                } else {
                    return this.getResponse("", 404);
                }
            }

            private LowLevelHttpResponse getResponse(String endpoint, int status) {
                return this.getResponse(endpoint, status, "", null);
            }

            private LowLevelHttpResponse getResponse(String endpoint,
                    int status, String body) {
                return this.getResponse(endpoint, status, body, null);
            }

            private LowLevelHttpResponse getResponse(String endpoint,
                    int status, String body, String content_type) {
                MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();

                response.addHeader("Content-Length",
                        String.valueOf(body.length()));
                response.setStatusCode(status);

                if (content_type != null) {
                    response.setContentType(content_type);
                } else {
                    response.setContentType("application/vnd.maxmind.com-"
                            + endpoint + "+json; charset=UTF-8; version=1.0");
                }

                response.setContent(body);
                return response;
            }
        };
    }
}