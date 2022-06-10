package model;

public class DownloadException extends Exception{

    public DownloadException(String message, Throwable err){
        super(message, err);
    }
}
