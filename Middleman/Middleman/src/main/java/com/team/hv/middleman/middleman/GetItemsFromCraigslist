// Constants & Variables

var KEY = "rit483d65-f477-4935-ac6d-35e12287a5b";                                   //API Key
var VER = "&SERVICE-VERSION=1.0.0"                                                  //Version
var NAM = "&SECURITY-APPNAME=rit483d65-f477-4935-ac6d-35e12287a5b"                  //App Name {basically our key}
var RES = "&RESPONSE-DATA-FORMAT=XML"                                               //Response Format
var PAY = "&REST-PAYLOAD"                                                           //Payload
var sBa = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME="  //Search Base String


    //Shopping Bit of API

        //Find Popular Items
        function findEbayProducts(search){
            
            
            
           $.ajax({
		type: "GET",
		url: "assets/php/proxyEbay.php?search=" + search,
		dataType: "xml",
		success: parseXml
	     });
	     
	     
        }
	
	function parseXml(xml) {
	console.log(xml);
	
	var numItems = 0;
	var price = 0;
	var maxPrice = 0;
	var minPrice = 99999999;
	
	$(xml).find("item").each(function() {
	    var condition =  $(this).find("conditionDisplayName").text();
	    //console.log(condition);
	    if((condition == 'New') || (condition == 'New other (see details)') || (condition == 'Manufacturer refurbished') || (condition == 'Used')){
		var currentPrice = $(this).find("currentPrice").text();
		currentPrice = parseInt(currentPrice);
		
		if(currentPrice > maxPrice){
		    maxPrice = currentPrice;
		}
		if(currentPrice < minPrice){
		    minPrice = currentPrice;
		}
		price += currentPrice;
		numItems++;
	    }
	});
	var avgPrice = price / numItems;
	
	//console.log('Highest price: ' + maxPrice);
	//console.log('Average price: ' + avgPrice);
	//console.log('Lowest price: ' + minPrice);
	
	//console.log(numItems);
	
	//$('#content').append('<p>Highest price: '+ maxPrice + '</p>');
	//$('#content').append('<p>Average price: '+ avgPrice + '</p>');
	//$('#content').append('<p>Lowest price: '+ minPrice + '</p>');
    }