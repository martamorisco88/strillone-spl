<?php

require 'vendor/autoload.php';

$KEY_REPUBBLICA_IT = 'repubblicait';
$KEY_GO_BARI = 'go_bari';
$KEY_GO_FASANO = 'go_fasano';
$KEY_FAVOLE_E_RACCONTI = 'favole_racconti';
$KEY_TEST_INGLESE = 'test_inglese';
$KEY_TEST_PORTOGHESE = 'test_portoghese';
$KEY_ISTRUZIONI_USO = 'istruzioni_uso';

$app = new \Slim\Slim();
$app->response->headers->set('Content-Type', 'text/xml');
# $app->config('debug', true);

$app->get('/newspapers', 'get_newspapers');
$app->get('/newspapers/:newspaper', 'get_newspaper');

$app->run();

function create_newspaper($doc, $lingua, $nome, $edizione, $resource, $id ,$beta = false) {
	$newspaper = $doc->createElement('testata');

	$newspaper->appendChild($doc->createElement('lingua', $lingua));
	$newspaper->appendChild($doc->createElement('nome', $nome));
	$newspaper->appendChild($doc->createElement('edizione', $edizione));
	$newspaper->appendChild($doc->createElement('resource', $resource));
	$idAttribute=$doc->createAttribute('id');
	$idAttribute->value=$id;
	$newspaper->appendChild($idAttribute);
	if ($beta)
		$newspaper->appendChild($doc->createElement('beta', 'true'));

	return $newspaper;
}

function get_newspapers() {
	global $KEY_REPUBBLICA_IT, $KEY_GO_BARI, $KEY_GO_FASANO, $KEY_FAVOLE_E_RACCONTI, $KEY_TEST_INGLESE, $KEY_TEST_PORTOGHESE, $KEY_ISTRUZIONI_USO;

	$today = date('Y-m-d');
	$doc = new DOMDocument('1.0', 'UTF-8');
	$testate = $doc->createElement('testate');

	$testate->appendChild(create_newspaper($doc, 'it', 'repubblica punto it', $today, $KEY_REPUBBLICA_IT ,hash('SHA256', $KEY_REPUBBLICA_IT)));
	$testate->appendChild(create_newspaper($doc, 'it', 'go bari', $today, $KEY_GO_BARI,hash('SHA256', $KEY_GO_BARI)));
	$testate->appendChild(create_newspaper($doc, 'it', 'go fasano', $today, $KEY_GO_FASANO,hash('SHA256', $KEY_GO_FASANO)));
	$testate->appendChild(create_newspaper($doc, 'it', 'favole e racconti', $today, $KEY_FAVOLE_E_RACCONTI,hash('SHA256', $KEY_FAVOLE_E_RACCONTI)));
	$testate->appendChild(create_newspaper($doc, 'en', 'test inglese', $today, $KEY_TEST_INGLESE,hash('SHA256',$KEY_TEST_INGLESE)));
	$testate->appendChild(create_newspaper($doc, 'pt', 'test portoghese', $today, $KEY_TEST_PORTOGHESE,hash('SHA256',$KEY_TEST_PORTOGHESE )));
	$testate->appendChild(create_newspaper($doc, 'it', 'istruzioni d\'uso strillone', $today, $KEY_ISTRUZIONI_USO,hash('SHA256',$KEY_ISTRUZIONI_USO )));
	
	
	$doc->appendChild($testate);
	echo $doc->saveXML();
}

function get_newspaper($newspaper) {
	$file = 'feeds/' . $newspaper . '.xml';
	$fp = fopen($file, 'r');
	$n = fread($fp, filesize($file));
	fclose($fp);

	echo $n;
}

?>
