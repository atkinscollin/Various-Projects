#import socket module 
import socket
import sys

HOST = '10.228.164.49'
PORT = 5550
bool = True

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print 'Socket created'

#Prepare a sever socket 
try:
    serverSocket.bind((HOST, PORT))
except IOError:
    print 'Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1]
    sys.exit()

print 'Socket binded'

serverSocket.listen(1)
print 'Socket listening'

while bool: 
    #Establish the connection 
    print 'Ready to serve...' 
    connectionSocket, addr = serverSocket.accept()
    print 'Connected with ' + addr[0] + ':' + str(addr[1])

    try:
        #Receive messsage and parse
        message = connectionSocket.recv(1024)
        print 'Message received: ' + message
        filename = message.split()[1] 
        f = open(filename[1:])
        print 'Filename: ' + filename[1:]
        outputdata = f.read()
        print 'Output data: ' + outputdata
        #Send one HTTP header line into socket 
        connectionSocket.send('\nHTTP/1.1 200 OK\n\n')
        #Send the content of the requested file to the client 
        connectionSocket.send(outputdata)
        connectionSocket.close()
    except IOError: 
        #Send response message for file not found 
        print '404 Not Found'
        connectionSocket.send('\nHTTP/1.1 404 Not Found\n\n')
        connectionSocket.close()
        bool = False

#Close socket 
serverSocket.close()
