package com.belbim.kopter.followme;

/**
 * Created by asay on 23.12.2014.
 */
public interface IDeviceServer {

    String touchServer(String uid, String deviceType);
    //uid cihaz tekil numarasi maks 45 hane, deviceType mk (mikrokopter) ya da mp (mobilephone)
    //KopterSession (json) sınıfı cinsinden dönüş yapılır.
    //KopterSession.deviceId: -2 mikrokopter kaydı yok, -3 mobile phone kaydı yok, -1 DB error
    //KopterSession.sessionId=-1 session oluşturulamadı.

    int registerDevice(String uid, String name, String deviceType);
    //uid cihaz tekil numarasi maks 45 hane, name cihaz ismi
    //deviceType mk (mikrokopter) ya da mp (mobilephone)
    //-1 ise db erişim hatası. -2 ya da -3 ise kaydı yok ancak yine de kayıt edilemedi.

    int sendStatus(String jsonStatus);
    //gelecek olan json KopterStatus sınıfındaki gibi olmalıdır. (parametre adı jsonstatus)
    //dönüş 1 ise başarılı işlem gerçekleştimiştir; dönüş 0 ise data kaydedilmemiştir.
    //dönüş -1 ise db bağlantısında sıkıntı bulunmaktadır.

    String getKopterStatus(int deviceId);
    //Kopter'in son bilinen durumunu bildirir. hata çıkmazsa json formatında KopterStatus döner
    //-1 db hatası hatası. -2 Koptere ait durum bilgisi yok.

    String getTask(int deviceID);
    //yalnizca mk tarafindan kullanilir. koptere iletilecek yeni bir
    //gorev varsa FollowMeData objesi json formatinda cevap donulur.
    //kopter için görev yoksa FollowMeData default değerler ile döner. (Lat0, long0 etc.) bu durumda
    //Kopter hedefe gönderilmemelidir.
    //-2 db connection Error
    //
    //GetTask içinde gelen veri :
    //      1 ise followme
    //      2 ise lookatme
    //      3 ise comehome

    //followme event'inde kopter'e  followme cihazının konumları Waypoint olarak gönderilir. aynı zamanda
    //      bearing de hesaplanır.
    //lookatme event'inde kopterStatus'tan alınan pozisyon verileri Waypoint olarak geri gönderilir
    //          (böylece kopter pozisyonu korunur), bununla birlikte bearing hesaplanır ve gönderilir.
    //comehome event'inde koptere, kopterStatus'tan alınan home pozisyonu (enlem,boylam) Waypoint olarak
    //          gönderilir. Home'a göre bearing hesaplanır.
    int setTask(int kopterId, int followMeDeviceId);
    //Değerler ile ilgili açıklamalar getTask() metodu içerisinde yapılmıştır.

    int sendFollowMeData(String jsonfollowme);
    //Yalnızca followMeDevice tarafından kullanılır.
    //FollowMeData sınıfındaki objeyi json olarak (parametre adı jsonfollowme) gonderir.
    //Başarılı gönderim halinde 0, hatalı json -2, db erişim hatası -1

    String getFollowMeData(int deviceId);
    //deviceId'si verilen FollowMe cihazının bilgileri döndürülür.
    //deviceId 0 girilirse tüm followme cihazlarına ait bilgiler array olarak döndürülür.
    //SQL hatası -1

    int getRouteId(int deviceId);
    //followMeDevice tarafından çağrılır. İlgili followMeDevice'ının session rotasını çıkartmak
    //amacıyla kullanılır. Bu fonksiyon sonrasında dönen integer, FollowMeData'sındaki route değişkenine
    //girilmelidir. -1 ise db hatası, -2 ise deviceId parametresi Integer değil.

    int sendLog(String logJson);
    //DeviceType DeviceTypes.java dosyasındaki gibi olacaktır. (MK ya da MP)
    //LogMessage.java cinsi obje json olarak gönderilir.
    //logLevel hata mesajları için 1, bilgi mesajları için 2, diğer mesajlar için 99 olacaktır.
    //logMessage için bir kısıtlama yoktur, istenen şey yazılabilir.
    //başarılı gönderim halinde 0, hatalı json -2, db erişim hatası -1 dönüş yapar.

    int endRoute(int routeId);
    //Rotanın sonuçlandırılması için followme aksiyonunun bitirilmesi ile çağırılan metoddur.
    //sonlandırılmak istenen rota gönderilir.
    //1: başarılı, -1 db erişim hatası ya da geçersiz rota id
}


