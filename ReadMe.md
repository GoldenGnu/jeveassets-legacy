
<br>
<hr />
<br>






<h1>Requirements</h1>

<ol><li>Java Runtime Environment (Get it free at: <a href='http://java.com/'>java.com</a>)<br>
</li><li>An active Eve-Online account (to have any use of the program)<br>
<br>
<hr />
<br></li></ol>







<h1>How to run</h1>

<h2>Windows</h2>

Double click on jeveassets.jar<br>
<br>
<h2>Linux</h2>

in terminal run:<br>
<pre>java -jar /path/to/jEveAssets/jeveassets.jar</pre>
where "path/to" is the path on your computer to the jeveassets directory<br>
<br>
<b>or</b>

cd to jEveAssets directory and run:<br>
<pre>java -jar jeveassets.jar</pre>
<br>
<hr />
<br>






<h1>Update jEveAssets to a new version</h1>

Simply overwrite all the old files with the new ones. Your settings and assets will stay untouched.<br>
<br>
<hr />
<br>






<h1>Sort columns</h1>

You need to double click to sort a new column. A single click will sub-sort the column.<br>
<br>
<hr />
<br>






<h1>Portable</h1>

Add the command line argument: -portable<br>
<br>
<pre>java -jar jeveassets.jar -portable</pre>
<br>
<hr />
<br>





<h1>Settings location</h1>

Replace <b>USERNAME</b> with your username<br>
<br>
<h2>Windows</h2>
XP: C:\Documents and Settings\<b>USERNAME</b>\.jeveassets\<br>
Vista: C:\Users\<b>USERNAME</b>\.jeveassets\<br>

<h2>Linux</h2>
/home/<b>USERNAME</b>/.jeveassets/<br>
<br>
<h2>Mac</h2>
~/Library/Preferences/JEveAssets<br>
<br>
<hr />
<br>





<h1>jEveAssets will not run</h1>

<h2>Solutions 1</h2>
Re-download jEveAssets (jEveAsset can get corrupted doing download)<br>
<br>
<ol><li>Download <a href='http://eve.nikr.net/jeveasset'>jEveAssets</a>
</li><li>Unzip<br>
</li><li>Double click on jeveasset.jar</li></ol>

<h2>Solutions 2</h2>
Re-install java (old versions of java are bad)<br>
<br>
<ol><li>Uninstall all versions of java (<a href='http://www.java.com/en/download/uninstall.jsp'>help</a>)<br>
</li><li>Download the latest version of <a href='http://www.java.com/en/download/index.jsp'>java</a>
</li><li>Install java (<a href='http://www.java.com/en/download/help/windows_manual_download.xml'>help</a>)<br>
<br>
<hr />
<br></li></ol>






<h1>File Lock</h1>
File locks are necessary to prevent file corruption.<br>
If jEveAssets is forcefully terminated while reading/writing a file, the file will never be unlocked.<br>
This prevent jEveAssets from accessing the file.<br>
<ol><li>Close all instances of jEveAssets (You can restart your computer to be 100% sure all instances of jEveAssets is closed)<br>
</li><li>Run a single instance of jEveAssets (This will delete all existing file locks)<br>
<br>
<hr />
<br></li></ol>






<h1>Character API Key</h1>
<b>Note:</b> step 1 and 2 can be skipped if you run jEveAssets for the first time.<br>
<ol><li>Go to Options > Accounts...<br>
</li><li>Click "Add"<br>
</li><li>Click on the "New API Key" link<br>
</li><li>Enter a name for the API Key<br>
</li><li>Click "Submit" on the webpage<br>
</li><li>Copy and Paste the Key ID and Verification Code in to jEveAssets<br>
</li><li>Click "Next" and wait...<br>
<ul><li>If you get an error see: <a href='https://code.google.com/p/jeveassets/wiki/ReadMe#API_Key_Troubleshooting'>API Key Troubleshooting</a>
</li></ul></li><li>Click "OK"</li></ol>

To have full use of jEveAssets you need:<br>
<ul><li><code>WalletTransactions</code>
</li><li><code>WalletJournal</code>
</li><li><code>MarketOrders</code>
</li><li><code>AccountBalance</code>
</li><li><code>Contracts</code>
</li><li><code>AssetList</code>
</li><li><code>IndustryJobs</code>
It will work with just the <code>AssetList</code>, but, with limited functionality.<br>
<br>
<hr />
<br></li></ul>





<h1>Corporation API Key</h1>
You need to create a Customizable API key for the corporation<br>
<ol><li>Go to <a href='http://community.eveonline.com/support/api-key/update'>http://community.eveonline.com/support/api-key/update</a>
</li><li>Select a Character that is CEO (Will have "Corp" after the name)<br>
</li><li>in "Type" select Corporation<br>
</li><li>Now select:<br>
<ul><li><code>WalletTransactions</code>
</li><li><code>WalletJournal</code>
</li><li><code>MarketOrders</code>
</li><li><code>AccountBalance</code>
</li><li><code>Contracts</code>
</li><li><code>AssetList</code>
</li><li><code>IndustryJobs</code>
</li></ul></li><li>Create the API Key by pressing "submit"<br>
</li><li>Enter the API Key in jEveAssets (Just like a Character API Key)<br>
</li><li>Update everything in jEveAssets, to get the corporation stuff<br>
<br>
<hr />
<br></li></ol>






<h1>API Key Troubleshooting</h1>
Solutions to common errors while importing API Keys<br>
<h2>API Proxy</h2>
Invalid API Proxy.<br>
<ol><li>In the menu select: <code>"Options"</code> > <code>"Options..."</code>
</li><li>Select <code>"Proxy"</code>
</li><li>Uncheck <code>"Enable API Proxy"</code> or make sure <code>"API Proxy Address"</code> is set correct.<br>
<h2>Proxy Server</h2>
If you're behind a proxy server, you need to enable it in jEveAssets.<br>
</li><li>In the menu select: <code>"Options"</code> > <code>"Options"</code>...<br>
</li><li>Select <code>"Proxy"</code>
</li><li>Select your <code>"Proxy Type"</code>
<ul><li><code>"Direct"</code> = No proxy server<br>
</li><li><code>"HTTP"</code> = Http proxy server<br>
</li><li><code>"SOCKS"</code> = Socks proxy server<br>
</li></ul></li><li>Enter your <code>"Proxy Address"</code> and <code>"Proxy Port"</code>
<h2>Firewall blocks Java</h2>
</li><li>Disable your firewall (at your own risk...)<br>
</li><li>Import API Keys in jEveAssets<br>
</li><li>Enable your firewall<br>
If you succeed, you need to add a firewall exception for Java (Please contact your firewall provider, for additional help)<br>
<h2>API is down</h2>
</li><li><a href='http://eve-offline.net/'>Check if the API is down</a><br>
</li><li>Wait until it's back up...<br>
<h2>API is unstable</h2>
Try a couple of times (Press <code>"Previous"</code> to retry...)<br>
<h2>API Error</h2>
This is a CCP API Error.<br>
Feel free to <a href='https://code.google.com/p/jeveassets/wiki/ReadMe#Asking_for_help'>ask for help</a> (please include the error code)<br>
<h2>I give up...</h2>
No problem! if you have tried everything above, please send a <a href='https://code.google.com/p/jeveassets/wiki/ReadMe#Bugs'>bug report</a>
<br>
<hr />
<br></li></ol>





<h1>Bugs</h1>

Please send an email to niklaskr@gmail.com and include the following:<br>
<ol><li>The jeveassets.log in the <a href='https://code.google.com/p/jeveassets/wiki/ReadMe#Settings_location'>settings location</a><br>
</li><li>Instructions on how to reproduce the bug<br>
<br>
<hr />
<br></li></ol>





<h1>Asking for help</h1>
If nothing of the above solve your problem.<br>
<br>
<ol><li>Make a post in the <a href='https://forums.eveonline.com/default.aspx?g=posts&t=6419'>forum thread</a>
</li><li>Please include the following information: Operation System and Java version (if you know)<br>
</li><li>I'll do my best to help you.<br>
<br>
<hr />