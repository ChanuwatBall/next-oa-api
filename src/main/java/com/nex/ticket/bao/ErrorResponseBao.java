package com.nex.ticket.bao;

public class ErrorResponseBao {
    private String error;

    public ErrorResponseBao() {
    }

    public ErrorResponseBao(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
