const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.calcPercent = functions.database.ref('/product/{uid}/').onWrite(event =>{

	var eventSnapshot = event.data;

	console.log("old Price exist",eventSnapshot.child('old_price').exists());
	console.log("new Price exist",eventSnapshot.child('price').exists());

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

exports.registerDate = functions.database.ref('/product/{uid}/').onWrite(event =>{

	//return event.data.ref.child('register_date').set(Date.now());
});