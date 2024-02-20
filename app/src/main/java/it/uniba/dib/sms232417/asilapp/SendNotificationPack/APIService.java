package it.uniba.dib.sms232417.asilapp.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    // QUESTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                    "Authorization:key=d049afee3537dc9d9c00c1656f29646062dff894" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
