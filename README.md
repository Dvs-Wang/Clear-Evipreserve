# Clear-Evipreserve
The project implemented a Lightweight and Manageable Architecture of Decentralized Digital Evidence Preservation System Coded in Java, which can protect user's digital evidence by creating proof of existence and proof of audit on the Bitcoin blockchain.
We use bitcoinj as the core library of our project, and take Netty as the Network solution. Convenient GUI is designed using JavaFx to offer user a clear undertanding and use of the software. All the functions provided by the software has been tested successfully on the Bitcoin public test network--Testnet3. To build a open and stable service, the code need to be further optimized and checked.<br> 
There exists three entities in the architecture:<br>
## EvidencePreservationAppClient :<br> 
the client for user to submit and manage their digital evidence safely and efficiently, here are some demos of the client:<br>
<img src="https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Offline%20Evidence%20Submission%20and%20Handling.gif" width = "600" alt="Offline Evidence Submission and Handling" /><br>
*Offline Evidence Submission and Handling*
<br>
<img src= "https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Logon%20Module%20of%20Client.gif" width = "600" alt="Logon Module of Client" /><br>
*Logon Module of Client*
<br>
<img src= "https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Online%20Evidence%20Management%20for%20Client.gif" width = "600" alt="Logon Module of Client" /><br>
*Online Evidence Management for Client*
<br>
<img src= "https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Forensic.gif" width = "600" alt="Forensics Work on Client" /><br>
*Forensics Work on Client*
<br>
## Server :<br> 
the public server to receive requests from clients and broadcast standard Bitcoin transactions to achieve the proofs, here is the demo of the server:<br>
<img src= "https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Server.gif" width = "600" alt="Server" /><br>
*Server*
<br>
## AuditServer :<br> 
The audit end is to assist legal institutions to give audit to the evidence preserved on the blockchain efﬁciently and securely, here are some demos of the audit end:<br>
<img src= "https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Audit%20Key%20Management.gif" width = "600" alt="Audit Key Management" /><br>
*Audit Key Management*
<br>
<img src= "https://github.com/Vivid-Wang/Clear-Evipreserve/blob/master/Demo%20gif%20and%20pictures/Evidence%20Auditing.gif" width = "600" alt="Evidence Auditing" /><br>
*Evidence Auditing*
<br>

Special thanks for Mike Hearn and his tutorial on Youtube which inspires us a lot on the software implementation of the architecture.
