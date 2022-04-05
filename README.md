# Policy Translation Point (PTP)

This repository contains 

PTP translates high-level security policies like “Do not record sound in the living room tonight” into low-level policies in a [XACML](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=xacml) formalism. Furthermore, PTP detects potential conflicts linke redundancies and inconsistencies between high-level policies. 

PTP is implemented through a client-server architecture:
- The [server](/server/) folder contains the source code for the web-server of PTP.
- The [client](/client/) folder contains the source code for the web-based graphical user interface of PTP.