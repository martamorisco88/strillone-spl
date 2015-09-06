
<?php
$KEY_REPUBBLICA_IT = 'repubblicait';
$KEY_GO_BARI = 'go_bari';
$KEY_GO_FASANO = 'go_fasano';
$KEY_FAVOLE_E_RACCONTI = 'favole_racconti';
$KEY_TEST_INGLESE = 'test_inglese';
$KEY_TEST_PORTOGHESE = 'test_portoghese';

$dir ='C:/xampp/htdocs/strillonews/feeds/';
$resources = scandir($dir); // ottengo i nomi dei file contenuti nella cartella feeds
$doc = new DOMDocument;



for ($i = 2, $n = count($resources) ; $i < $n ; $i++)
{

    $doc->load($dir.$resources[$i]);

    //elimino l'enstensione dal nome del file
    $resource=substr($resources[$i],0, strlen($resources[$i])-4); 

	//inserisco il campo id al nodo giornale
	$giornale=$doc->getElementsByTagName('giornale')->item(0);
	$hash_id=hash('SHA256',$resource);
	
	$idAttribute=$doc->createAttribute('id');
	$idAttribute->value=$hash_id;
	$giornale->appendChild($idAttribute);
	
	//Ottengo il numero delle sezioni contenute nel file
	$m=0;
	$sezioni = $doc->getElementsByTagName('sezione');
	foreach ($sezioni as $sezione) {
	
		$m++;
	}
	//Riempio un array di contentente i nomi delle sezioni
	$nomi = $doc->getElementsByTagName('nome');
	foreach ($nomi as $nome) {
	
		$nomi_sezioni[]=$nome->nodeValue;
	}
	
	
	
	for ($l = 0; $l <= $m-1; $l++) {
	
		$sezioni = $doc->getElementsByTagName('sezione')->item($l);
		$stringa_id=$resource;
		$stringa_id .=$nomi_sezioni[$l];
		$hash_id=hash('SHA256', $stringa_id);
		
		$idAttribute=$doc->createAttribute('id');
		$idAttribute->value=$hash_id;
		$sezioni->appendChild($idAttribute);

	}
	
	
	$doc->saveXML();
	$doc->save($dir.$resources[$i]); 
}

?>