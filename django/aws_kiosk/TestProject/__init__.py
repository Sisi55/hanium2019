import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('kiosk-firestore-jnsy-bcfe4-firebase-adminsdk-269q4-4a795774bb.json')
firebase_admin.initialize_app(cred)