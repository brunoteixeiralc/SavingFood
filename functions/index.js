const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.calcPercent = functions.database.ref('/discounts/{uid}/').onWrite(event =>{

	var eventSnapshot = event.data;

	console.log("old price exist",eventSnapshot.child('old_price').exists());
	console.log("new price exist",eventSnapshot.child('price').exists());

	if(eventSnapshot.child('old_price').exists() && eventSnapshot.child('price').exists()){

		const old_price = eventSnapshot.child('old_price').val();
		console.log("old price",old_price);
		
		const new_price = eventSnapshot.child('price').val();
		console.log("new price",new_price);

		const percent = (((old_price - new_price) * 100)/old_price)
		console.log("Percent",percent);

		return event.data.ref.child('percent').set(percent);
	}

	return;

});

exports.sendAlertPush = functions.database.ref('/discounts/{uid}/').onWrite(event =>{

	let productCreated = false;
	let productChanged = false;

	let productData = event.data.val();

	let productName = event.data.child('name').val();

	if(!event.data.previous.exists()){
		productCreated = true;
		console.log("New Product",productName);
	}else{
		return;
	}

	if(!productCreated && event.data.changed()){
		productChanged = true;
		console.log("Exist Product",productName);
		return;
	}

	return loadUsers(productName).then(tokens => {
	
	  	let payload = {
				notification: {
			    title: 'Saving Foods - Alerta',
			    body: productName + ' na lista.',
			    sound: 'default',
			    badge: '1'
			    }
			};

		if(tokens.length > 0){
			console.log("Send to",tokens.length + ' users');
			return admin.messaging().sendToDevice(tokens, payload);
		}else{
			return;
		}
   
	});

});

function loadUsers(productName) {
	let dbRef = admin.database().ref('/alert').child(productName);

	let defer = new Promise((resolve, reject) => {
		dbRef.once('value', (snap) => {
			let data = snap.val();
      let tokens = [];
      for (var property in data) {
	      tokens.push(data[property]);
      }
			resolve(tokens);
		}, (err) => {
			reject(err);
		});
	});
	return defer;
}
