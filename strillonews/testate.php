<?

function get_testate()  {
$today = date("Y-m-d");

$file_xml = "<?xml version='1.0' encoding='ISO-8859-1'?>
<testate>
	<testata>
		<lingua>it</lingua>
		<nome>go bari</nome>
		<edizione>" . $today . "</edizione>
		<url>http://www.walks.to/strillone/get_feed.php?giornale=go%20bari</url>
	</testata>

	<testata>
		<lingua>it</lingua>
		<nome>go fasano</nome>
		<edizione>" . $today . "</edizione>
		<url>http://www.walks.to/strillone/get_feed.php?giornale=go%20fasano</url>
	</testata>

	<testata>
		<lingua>it</lingua>
		<nome>favole e racconti</nome>
		<edizione>" . $today . "</edizione>
		<url>http://www.walks.to/strillone/feeds/favole_racconti.xml</url>
	</testata>
	
	<testata>
		<lingua>en</lingua>
		<nome>test inglese</nome>
		<edizione>" . $today . "</edizione>
		<url>http://www.walks.to/strillone/feeds/test_inglese.xml</url>
	</testata>
	
	<testata>
		<lingua>pt</lingua>
		<nome>test portoghese</nome>
		<edizione>" . $today . "</edizione>
		<url>http://www.walks.to/strillone/feeds/test_portoghese.xml</url>
	</testata>
	
	<testata>
		<lingua>it</lingua>
		<nome>test repubblica</nome>
		<edizione>" . $today . "</edizione>
		<url>http://www.walks.to/strillone/feeds/test_repubblica.xml</url>
	</testata>

</testate>
";

header('Content-Type: text/xml; charset=iso-8859-1');
return $file_xml;
}
