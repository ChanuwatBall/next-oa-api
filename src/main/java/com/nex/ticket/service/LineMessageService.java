package com.nex.ticket.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class LineMessageService {

    @Value("${line.access-token}")
    private String channelAccessToken;

    private static final String LINE_API_URL = "https://api.line.me/v2/bot/message/push";

    public void sendLineMessage(String lineUserId, String message) {
        // For testing, we will just print the message instead of sending it to LINE API
        System.out.println("Sending LINE message to user " + lineUserId + ": " + message);
        String payload = createMessagePayload(lineUserId, message);
        // String channelAccessToken = company.getChannelToken();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + channelAccessToken);

        // Create the HttpEntity with the body and headers
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        // Make a POST request to the LINE API
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(LINE_API_URL, HttpMethod.POST, entity, String.class);
    }

    private String createMessagePayload(String userId, String messageText) {
        return "{\n" +
                "    \"to\": \"" + userId + "\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"" + messageText + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    public void sendCarouselMessage(String userId) {
        try {
            if (channelAccessToken == null || channelAccessToken.isBlank()) {
                throw new RuntimeException("LINE channel access token is missing");
            }

            String payload = createCarouselMessagePayload(userId);
            // System.out.println("payload = " + payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(channelAccessToken);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.exchange(
                    LINE_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class).getBody();

            // System.out.println("LINE response = " + response);

        } catch (HttpClientErrorException e) {
            System.out.println("LINE error status = " + e.getStatusCode());
            System.out.println("LINE error body = " + e.getResponseBodyAsString());
            throw e;
        }
    }

    private String createCarouselMessagePayload(String userId) {
        String flexJson = "{\n" +
                "  \"type\": \"bubble\",\n" +
                "  \"size\": \"mega\",\n" +
                "  \"header\": {\n" +
                "    \"type\": \"box\",\n" +
                "    \"layout\": \"vertical\",\n" +
                "    \"backgroundColor\": \"#9A1919\",\n" +
                "    \"paddingAll\": \"16px\",\n" +
                "    \"contents\": [\n" +
                "      {\n" +
                "        \"type\": \"text\",\n" +
                "        \"text\": \"Nex Express\",\n" +
                "        \"color\": \"#ffffff\",\n" +
                "        \"weight\": \"bold\",\n" +
                "        \"size\": \"lg\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"text\",\n" +
                "        \"text\": \"ยืนยันการจองเที่ยวรถบัส\",\n" +
                "        \"color\": \"#FFEAEA\",\n" +
                "        \"size\": \"sm\",\n" +
                "        \"margin\": \"sm\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"type\": \"box\",\n" +
                "    \"layout\": \"vertical\",\n" +
                "    \"paddingAll\": \"16px\",\n" +
                "    \"spacing\": \"md\",\n" +
                "    \"contents\": [\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"vertical\",\n" +
                "        \"backgroundColor\": \"#F8F8F8\",\n" +
                "        \"cornerRadius\": \"12px\",\n" +
                "        \"paddingAll\": \"12px\",\n" +
                "        \"spacing\": \"sm\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"รหัสการจอง: NEXA123456\",\n" +
                "            \"weight\": \"bold\",\n" +
                "            \"size\": \"md\",\n" +
                "            \"color\": \"#222222\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"สถานะ: ชำระเงินแล้ว\",\n" +
                "            \"size\": \"sm\",\n" +
                "            \"color\": \"#1B8F3A\",\n" +
                "            \"weight\": \"bold\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"baseline\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"ต้นทาง\",\n" +
                "            \"size\": \"sm\",\n" +
                "            \"color\": \"#888888\",\n" +
                "            \"flex\": 2\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"ภูเก็ต\",\n" +
                "            \"size\": \"md\",\n" +
                "            \"weight\": \"bold\",\n" +
                "            \"color\": \"#111111\",\n" +
                "            \"flex\": 5\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"baseline\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"ปลายทาง\",\n" +
                "            \"size\": \"sm\",\n" +
                "            \"color\": \"#888888\",\n" +
                "            \"flex\": 2\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"หาดใหญ่\",\n" +
                "            \"size\": \"md\",\n" +
                "            \"weight\": \"bold\",\n" +
                "            \"color\": \"#111111\",\n" +
                "            \"flex\": 5\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"separator\",\n" +
                "        \"margin\": \"md\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"horizontal\",\n" +
                "        \"spacing\": \"md\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"box\",\n" +
                "            \"layout\": \"vertical\",\n" +
                "            \"flex\": 1,\n" +
                "            \"contents\": [\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"วันที่เดินทาง\",\n" +
                "                \"size\": \"xs\",\n" +
                "                \"color\": \"#888888\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"15 มี.ค. 2026\",\n" +
                "                \"size\": \"sm\",\n" +
                "                \"weight\": \"bold\",\n" +
                "                \"margin\": \"sm\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"box\",\n" +
                "            \"layout\": \"vertical\",\n" +
                "            \"flex\": 1,\n" +
                "            \"contents\": [\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"เวลาออก\",\n" +
                "                \"size\": \"xs\",\n" +
                "                \"color\": \"#888888\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"08:30 น.\",\n" +
                "                \"size\": \"sm\",\n" +
                "                \"weight\": \"bold\",\n" +
                "                \"margin\": \"sm\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"horizontal\",\n" +
                "        \"spacing\": \"md\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"box\",\n" +
                "            \"layout\": \"vertical\",\n" +
                "            \"flex\": 1,\n" +
                "            \"contents\": [\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"ที่นั่ง\",\n" +
                "                \"size\": \"xs\",\n" +
                "                \"color\": \"#888888\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"A3, A4\",\n" +
                "                \"size\": \"sm\",\n" +
                "                \"weight\": \"bold\",\n" +
                "                \"margin\": \"sm\"\n" +
                "              }\n" +
                "            ]\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"box\",\n" +
                "            \"layout\": \"vertical\",\n" +
                "            \"flex\": 1,\n" +
                "            \"contents\": [\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"ผู้โดยสาร\",\n" +
                "                \"size\": \"xs\",\n" +
                "                \"color\": \"#888888\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"text\",\n" +
                "                \"text\": \"2 คน\",\n" +
                "                \"size\": \"sm\",\n" +
                "                \"weight\": \"bold\",\n" +
                "                \"margin\": \"sm\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"separator\",\n" +
                "        \"margin\": \"md\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"baseline\",\n" +
                "        \"margin\": \"sm\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"จุดขึ้นรถ\",\n" +
                "            \"size\": \"sm\",\n" +
                "            \"color\": \"#888888\",\n" +
                "            \"flex\": 2\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"สถานีขนส่งภูเก็ต แพลตฟอร์ม 5\",\n" +
                "            \"size\": \"sm\",\n" +
                "            \"wrap\": true,\n" +
                "            \"color\": \"#222222\",\n" +
                "            \"flex\": 5\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"baseline\",\n" +
                "        \"margin\": \"sm\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"ราคา\",\n" +
                "            \"size\": \"sm\",\n" +
                "            \"color\": \"#888888\",\n" +
                "            \"flex\": 2\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"800.00 บาท\",\n" +
                "            \"size\": \"md\",\n" +
                "            \"weight\": \"bold\",\n" +
                "            \"color\": \"#9A1919\",\n" +
                "            \"flex\": 5\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"box\",\n" +
                "        \"layout\": \"vertical\",\n" +
                "        \"margin\": \"lg\",\n" +
                "        \"backgroundColor\": \"#FFF7E8\",\n" +
                "        \"cornerRadius\": \"10px\",\n" +
                "        \"paddingAll\": \"12px\",\n" +
                "        \"contents\": [\n" +
                "          {\n" +
                "            \"type\": \"text\",\n" +
                "            \"text\": \"กรุณามาถึงก่อนรถออกอย่างน้อย 30 นาที\",\n" +
                "            \"size\": \"xs\",\n" +
                "            \"color\": \"#8A6D3B\",\n" +
                "            \"wrap\": true\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"footer\": {\n" +
                "    \"type\": \"box\",\n" +
                "    \"layout\": \"vertical\",\n" +
                "    \"spacing\": \"sm\",\n" +
                "    \"paddingAll\": \"16px\",\n" +
                "    \"contents\": [\n" +
                "      {\n" +
                "        \"type\": \"button\",\n" +
                "        \"style\": \"primary\",\n" +
                "        \"color\": \"#9A1919\",\n" +
                "        \"action\": {\n" +
                "          \"type\": \"uri\",\n" +
                "          \"label\": \"ดูรายละเอียดการจอง\",\n" +
                "          \"uri\": \"https://nexexpress.com/booking/NEXA123456\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"button\",\n" +
                "        \"style\": \"secondary\",\n" +
                "        \"action\": {\n" +
                "          \"type\": \"uri\",\n" +
                "          \"label\": \"ดาวน์โหลด E-Ticket\",\n" +
                "          \"uri\": \"https://nexexpress.com/e-ticket/NEXA123456\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        return "{\n" +
                "  \"to\": \"" + "U10d48c671adf2ca9f94d1e0f2826501e" + "\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"type\": \"flex\",\n" +
                "      \"altText\": \"ข้อมูลการจองเที่ยวรถบัส\",\n" +
                "      \"contents\": " + flexJson + "\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
