import firebase_admin
from celery import Celery
from firebase_admin import messaging, credentials

celery = Celery('task', broker="localhost")
cred = credentials.Certificate("/home/iems5722/iems5722-55842-firebase-adminsdk-3mzj0-a620427e0d.json")
# cred = credentials.Certificate("C:/Users/ASUS/Downloads/iems5722-55842-firebase-adminsdk-3mzj0-a620427e0d.json")
firebase_admin.initialize_app(cred)


@celery.task
def sendPush(title, msg, registration_token, dataObject=None):
    # See documentation on defining a message payload.
    message = messaging.MulticastMessage(
        notification=messaging.Notification(
            title=title,
            body=msg
        ),
        data=dataObject,
        tokens=registration_token,
    )

    # Send a message to the device corresponding to the provided
    # registration token.
    response = messaging.send_multicast(message)
    # Response is a message ID string.
    print('Successfully sent message:', response)
