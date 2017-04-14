package com.biz.MQ;
/*
 * 这一版只实现了具体功能，并未对User_name等进行优化，排除可能出现的bug。对数据持久化和具体安全方面
 * 做的完善。
 * */
import java.io.IOException;
import java.util.Scanner;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.UnsupportedEncodingException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
public class Fanout {
	private final static String EXCHANGE_NAME = "log";
	/*
	 * 发送
	 */
	public static void send (String str) throws IOException, Exception
	{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout" );
			String message = str;
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
			channel.close();
			connection.close();
	}
	/*
	 * 接收
	 */
	public static void recive () throws java.io.IOException,
	java.lang.InterruptedException, Exception{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, "");
		Consumer consumer = new DefaultConsumer(channel) {
		      @Override
		public void handleDelivery(String consumerTag, Envelope envelope,
		      AMQP.BasicProperties properties, byte[] body) throws UnsupportedEncodingException{
		    	  String message = new String(body, "UTF-8");
		    	  System.out.println("'" + message + "'");
		      }
		    };
		    channel.basicConsume(queueName, true, consumer);
	}
	public static void main( String[] args ) throws IOException, InterruptedException, Exception
    {
		recive();
    	Scanner scanner=new Scanner(System.in);
        System.out.println("please input your name");
        String user_name=scanner.nextLine();
        System.out.println("now you can input what you want input 'q' to exit");
        String exit=""+user_name+" said q";
        while (true){
        	String message=scanner.nextLine();
        	message=""+user_name+" said "+message;
        	if(message.equals(exit)){
        		System.exit(0);//这里直接终结，在Java7以后是可以自动关闭连接的，但是不推荐这样，
        		//可能会有意想不到的问题。下一版本直接在主函数中建立connection、channel，公用一个
        		//方便管理也更安全。Python版不存在这样的问题，时间来不及啊，先这样。
        		break;
        	}
        	else{
        		send(message);
        	}
        }
    }
}


