import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class ClientPair implements Runnable {
  private static int count = 1;
  private static int gaming = 0;
  private static long totalTime = 0;
  String userOneToken;
  String userTwoToken;
  String userOneReceive;
  String userTwoReceive;
  Socket userOneSocket;
  Socket userTwoSocket;
  PrintWriter userOneOutput;
  PrintWriter userTwoOutput;
  BufferedReader userOneInput;
  BufferedReader userTwoInput;
  int users;

  public static synchronized void setCount(boolean add) {
    if (add)
      count += 2;
    else
      count -= 2;
  }


  @Override
  public void run() {
    long start = System.currentTimeMillis();
    users = count;
    userOneToken = getToken(count);
    userTwoToken = getToken(count + 1);
    setCount(true);

    try {
      login(1);
      login(2);
      /* handle list null */
      userOneReceive = userOneInput.readLine();
      while (new JSONObject(userOneReceive).isNull("list"))
        userOneReceive = userOneInput.readLine();

      /* send invite */
      userOneOutput.println("{\"invite\":\"speed" + (users + 1) + "\"}");
      Thread.sleep(500);
      userTwoInput.readLine();
      userTwoOutput.println("{\"invite\":\"speed" + (users) + "\"}");

      /* handle result1 */
      userTwoInput.readLine();
      userTwoOutput.println("{\"accept\":true}");

      /* get result 0 and whoFirst */
      userTwoInput.readLine();
      userOneInput.readLine();
      userOneReceive = userOneInput.readLine();
      userTwoReceive = userTwoInput.readLine();
      System.out.println(userOneReceive);
      System.out.println(userTwoReceive);

      if (new JSONObject(userTwoReceive).getBoolean("whoFirst"))
        userTwoOutput.println("{\"PutItThere\":true,\"data\":\"" + (users + 1) + "\"}");
      gaming++;
      System.out.println((System.currentTimeMillis() - start) / 1000F);
      totalTime += (System.currentTimeMillis() - start) / 1000F;
      if ((totalTime / gaming) > 6000)
        System.out.println("target reached!, final result = " + (totalTime / gaming));

      System.out.println(users + "," + (users + 1) + " start send data, current gaming pair ("
          + gaming + ")" + "current average process time is " + (totalTime / gaming) + "s");
      while (true) {
        userOneOutput.println("{\"PutItThere\":true,\"data\":\"" + (users) + "\"}");
        userTwoOutput.println("{\"PutItThere\":true,\"data\":\"" + (users + 1) + "\"}");
        Thread.sleep(1000);
      }

    } catch (Exception e) {
      System.err
          .println(users + "," + (users + 1) + " error, current gaming pair (" + gaming + ")");
      setCount(false);
      return;
    }
  }

  private String getToken(int user) {
    String token = null;
    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpGet getRequest =
        new HttpGet("http://fgc.heapthings.com/api/getToken?username=speed" + user
            + "&password=test1&gameID=fgcChess");
    try {
      token = EntityUtils.toString(httpClient.execute(getRequest).getEntity());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new JSONObject(token).getString("token");
  }

  private void login(int user) {
    try {
      if (user == 1) {
        userOneSocket = new Socket("fgc.heapthings.com", 5566);
        userOneInput = new BufferedReader(new InputStreamReader(userOneSocket.getInputStream()));
        userOneOutput = new PrintWriter(userOneSocket.getOutputStream(), true);
        userOneOutput.println("{\"token\":\"" + userOneToken + "\",\"gameID\":\"fgcChess\"}");
      } else {
        userTwoSocket = new Socket("fgc.heapthings.com", 5566);
        userTwoInput = new BufferedReader(new InputStreamReader(userTwoSocket.getInputStream()));
        userTwoOutput = new PrintWriter(userTwoSocket.getOutputStream(), true);
        userTwoOutput.println("{\"token\":\"" + userTwoToken + "\",\"gameID\":\"fgcChess\"}");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
