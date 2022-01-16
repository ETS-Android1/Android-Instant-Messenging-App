package hk.edu.cuhk.ie.iems5722.Group31;

public class MessageModel {
    public String message;
    public String time_now;
    public String name;

    public MessageModel(String message, String time_now, String name){
        this.message = message;
        this.time_now = time_now;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getTime_now() {
        return time_now;
    }

    public String getName() {
        return name;
    }
}
