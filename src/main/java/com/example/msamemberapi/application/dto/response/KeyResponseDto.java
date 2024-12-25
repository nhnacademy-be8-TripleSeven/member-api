package com.example.msamemberapi.application.dto.response;



public class KeyResponseDto {

    private Header header;
    private Body body;

    public Body getBody() {
        return body;
    }

    public Header getHeader() {
        return header;
    }

    public static class Body {

        private String secret;

        public String getSecret() {
            return secret;
        }
    }

    public static class Header {

        private Integer resultCode;
        private String resultMessage;
        private boolean isSuccessful;

        public Integer getResultCode() {
            return resultCode;
        }

        public String getResultMessage() {
            return resultMessage;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }
}
