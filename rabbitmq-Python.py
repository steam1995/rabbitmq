# -*- coding: utf-8 -*-
"""
Created on Wed Apr 12 20:33:06 2017

@author: Administrator
"""
import pika
import sys
import time
import threading,time
from time import sleep, ctime
def recive():
    connection=pika.BlockingConnection(pika.ConnectionParameters(
                host='localhost'))
    channel=connection.channel()
    channel.exchange_declare(exchange='log',
                             type='fanout')
    result=channel.queue_declare(exclusive=True)
    queue_name = result.method.queue
    channel.queue_bind(exchange='log',
                       queue=queue_name)
    def callback(ch,method,properties,body):
        print body
    channel.basic_consume(callback,
                          queue=queue_name,
                          no_ack=True)
    channel.start_consuming()
    
def send(message):
    connection=pika.BlockingConnection(pika.ConnectionParameters(
                host='localhost'))
    channel=connection.channel()
    channel.exchange_declare(exchange='log',
                             type='fanout')
    channel.basic_publish(exchange='log',
                              routing_key='',
                              body=message)
    connection.close()
    

print ('welcome please input your name')
username=raw_input(':')
print ('now can input what you want')
th1= threading.Thread(target=recive,args= ())
th1.start()
while True:
    message=username+' said '+raw_input(':')
    if(message==username+' said q'):
        break
    else:
        send(message)
time.sleep(1)
th1._Thread__stop()



    
    
    
    
    
    
    
    

