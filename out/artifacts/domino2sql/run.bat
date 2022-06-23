@echo off
%1 mshta vbscript:CreateObject("WScript.Shell").Run("%~s0 ::",0,FALSE)(window.close)&&exit
java -DDominoHost="192.168.210.153:9898" -DDominoUser="Admin" -DDominoPassword="Fjsft_123" -DDominoPath="./arc.sql.config.sft.approval.json"  -jar ./domino2sql-app.jar > ./server.log 2>&1 &
exit