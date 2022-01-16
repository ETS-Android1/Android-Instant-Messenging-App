package hk.edu.cuhk.ie.iems5722.Group31;

import android.app.Application;

public class UserInfo extends Application{
    public int id;
    public String nickName;
    public void setId(int id) {
        this.id = id;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}

