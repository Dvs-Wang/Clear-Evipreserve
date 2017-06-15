# Clear-Evipreserve
The project implemented a Lightweight and Manageable Architecture of Decentralized Digital Evidence Preservation System Coded in Java, which can protect user's digital evidence by creating proof of existence and proof of audit on the Bitcoin blockchain.
We use bitcoinj as the core library of our project, and take Netty as the Network solution. Convenient GUI is designed using JavaFx to offer user a clear undertanding and use of the software. All the functions provided by the software has been tested successfully on the Bitcoin public test network--Testnet3. To build a open and stable service, the code need to be further optimized and checked.<br> 
There exists three entities in the architecture:<br>
* EvidencePreservationAppClient :<br> 
the client for user to submit and manage their digital evidence safely and efficiently, here are some demos of the client:<br>
![](https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Offline%20Evidence%20Submission%20and%20Handling.gif "Offline Evidence Submission and Handling")
![](https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Logon%20Module%20of%20Client.gif "Logon Module of Client")
![](https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Online%20Evidence%20Management%20for%20Client.gif "Online Evidence Management for Client")
![](https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Forensic.gif "Forensics Work on Client")
* Server :<br> 
the public server to receive requests from clients and broadcast standard Bitcoin transactions to achieve the proofs, here is the demo of the server:<br>
![](https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Server.gif "Server")
* AuditServer :<br> 
The audit end is to assist legal institutions to give audit to the evidence preserved on the blockchain efﬁciently and securely, here are some demos of the audit end:<br>
![](https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Audit%20Key%20Management.gif "Audit Key Management")
![](https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Evidence%20Auditing.gif "Evidence Auditing")
<br>
Special thanks for Mike Hearn and his tutorial on Youtube which inspires us a lot on the software implementation of the architecture.
