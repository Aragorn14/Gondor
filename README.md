# Scube Android

Shared Preferences:
Shared Preferences are akin to global variables. Here we have three shared preferences:
1) User Shared Preferences
    > user_session: To track if the user has an existing session.
    > scube_id:     To retrieve the scube id of the user.
    > geny_imei:    To store the randomly generated 15 digit IMEI number for genymotion.
    > user_domain:  Dev or Production domain to use.
    > user_logout:  Used to propagate user logout through the app's activities.
    > user_id:      Currently this is the user's email id.

2) Device Shared Preferences
    > scube_device_id:      Device id of the user's device.
    > gcm_registration_id:  To know the registration ID of the user's device with GCM.

3) Navigation Shared Preferences
    > sp_latest_activity:   To keep traack of the latest activity of the app
    > cameFromNotification: Flag to know if the navigation started from clicking of the notification bar.
    > appVersion:           Integer to retrieve app version.

