const express = require('express')
const fs = require('fs')
const https = require('https')

const port = 4000

const app = express()

const options = {
  key: fs.readFileSync('key.pem'),
  cert: fs.readFileSync('cert.pem')
};

//FIREBASE BEGIN
const admin = require('firebase-admin');
const serviceAccount = require("./serviceAccountKey.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://lfgg-78154-default-rtdb.firebaseio.com",
    storageBucket: "https://console.firebase.google.com/project/lfgg-78154/storage/lfgg-78154.appspot.com/files"
  });

const db = admin.database();
const bucket = admin.storage().bucket()
//FIREBASE END


//GET APIS BEGIN
app.get('/', (req, res)=>{
  res.send("LFGG");
});


//GET APIS END

//POST APIS BEGIN
app.post('/post', (req, res)=>{
  let request = req.body;

  let name = request.name;
  let game = request.game;

  res.send(name + " " + game);

});

//POST APIS END


const server = https.createServer(options, app);
server.listen(port, () => console.log(`Running on https://localhost:${port}`))