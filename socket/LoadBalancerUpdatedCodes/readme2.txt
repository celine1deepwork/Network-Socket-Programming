Round-Robin Load Balancing Method is used. 
Client.java 
and 
Server.java 
are the updated codes with the Load Balancing Methods. 


Here's other info's about other Methodologies with Pro's and Con's : 

In the provided implementation, I used a simple round-robin load balancing method. This is a straightforward and commonly used method where the load balancer cycles through the available servers in a circular order, distributing incoming client requests evenly across the servers.

Here's a brief overview of the round-robin load balancing method and other methods that can be considered:

1. Round-Robin Load Balancing

Description: This method assigns client requests to servers sequentially. After the last server in the list is reached, the assignment starts over from the first server.
Advantages: Simple to implement, ensures even distribution of requests.
Disadvantages: Does not consider the current load or performance of servers.

2. Least Connections Load Balancing

Description: This method directs traffic to the server with the fewest active connections.
Advantages: Helps ensure that no single server is overwhelmed if there is an uneven distribution of request handling times.
Disadvantages: Requires monitoring the number of active connections on each server.

3. Least Response Time Load Balancing

Description: This method sends requests to the server with the lowest average response time.
Advantages: Can provide better performance by directing traffic to the most responsive servers.
Disadvantages: Requires monitoring and calculating the response times for each server.

4. Weighted Round-Robin Load Balancing

Description: Similar to round-robin but assigns more requests to servers with higher capacities or performance ratings.
Advantages: Balances load more effectively if servers have different capacities.
Disadvantages: Requires configuring weights for each server.

5. IP Hash Load Balancing

Description: Uses a hash of the client's IP address to determine which server should handle the request.
Advantages: Ensures that the same client IP is always directed to the same server, which can be useful for session persistence.
Disadvantages: Can lead to uneven distribution if there are many clients from the same IP range.