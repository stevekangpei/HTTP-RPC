# Overview
HTTP-RPC is a mechanism for executing remote procedure calls via HTTP. It combines the flexibility of SOAP with the simplicity of REST, allowing callers to invoke arbitrary operations on a remote endpoint using human-readable URLs and JSON rather than complex XML messages and descriptors. Any platform capable of submitting an HTTP request and consuming a JSON response can be an HTTP-RPC client, and any platform that can respond to an HTTP request and produce a JSON result can act as an HTTP-RPC server.

In HTTP-RPC, remote procedures, or "methods", are encapsulated by a "service", which is simply a collection of related operations. The service and method names are specified in the path component of the request URL. Method arguments are passed either via the query string or in the request body, like an HTML form. Method results are typically returned as JSON values, although methods that do not return a value are also supported.

For example, a GET request for the following URL might invoke the "add" method of a hypothetical "math" service:

    /math/add?a=1&b=2
    
The values 1 and 2 are passed as the "a" and "b" arguments to the method, respectively, with the service returning the value 3 in response. Alternatively, the same result could be obtained by submitting a POST request with a content type of `application/x-www-form-urlencoded` and a request body of `a=1&b=2`.

Method arguments may be any simple type, including number, boolean, and string. Indexed and keyed collections of simple types are also supported. As with any HTTP request, values that include reserved characters must be URL-encoded.

Indexed collection arguments are specified by providing zero or more values for a given parameter. For example, the "add" method above could be modified to accept a list of numbers to add rather than two fixed argument values:

    /math/addValues?values=1&values=2&values=3
    
Elements of keyed collections are represented as colon-delimited key/value pairs. For example, the following URL might represent a method named "translate" that accepts two keyed collection arguments named "point1" and "point2". The argument values themselves each contain an "x" and a "y" element:

    /math/translate?point1=x:5&point1=y:10&point2=x:2&point2=y:4

Omitting a value for a simple parameter type produces a null argument value for that parameter. Omitting all values for a collection parameter produces an empty collection argument for the parameter.

Methods may return any valid JSON type including number, boolean, string, array, and object. No content is returned by a method that does not produce a value.

An HTTP 200 status is returned on successful completion, and HTTP 500 is returned in the case of an error (i.e. an exception). Note that exceptions are intended to represent unexpected failures, not application-specific errors.
        
## Implementations
Support currently exists for implementing HTTP-RPC services in Java, and consuming services in Java, Objective-C/Swift, or JavaScript. For examples and additional information, please see the [wiki](https://github.com/gk-brown/HTTP-RPC/wiki).

I am always looking for ways to improve HTTP-RPC. If you have any suggestions, please [let me know](mailto:gk_brown@verizon.net?subject=HTTP-RPC).

# Java Server
The Java server implementation of HTTP-RPC allows developers to create and publish HTTP-RPC web services in Java. It is distributed as a JAR file that contains the following classes:

* _`org.httprpc`_
    * `WebService` - abstract base class for HTTP-RPC services
    * `Attachment` - interface representing an attachment
    * `RequestDispatcherServlet` - servlet that dispatches requests to service instances
* _`org.httprpc.beans`_
    * `BeanAdapter` - wrapper class that presents the contents of a Java Bean instance as a map, suitable for serialization to JSON
* _`org.httprpc.sql`_
    * `ResultSetAdapter` - wrapper class that presents the contents of a JDBC result set as an iterable list, suitable for streaming to JSON
    * `Parameters` - class for simplifying execution of prepared statements
* _`org.httprpc.util`_
    * `IteratorAdapter` - wrapper class that presents the contents of an iterator as an iterable list, suitable for streaming to JSON

Each of these classes is discussed in more detail below. 

The JAR file for the Java server implementation of HTTP-RPC can be downloaded [here](https://github.com/gk-brown/HTTP-RPC/releases). Java 8 and a servlet container supporting servlet specification 3.1 (e.g. Tomcat 8) or later are required.

## WebService Class
`WebService` is an abstract base class for HTTP-RPC web services. All services must extend this class and must provide a public, zero-argument constructor.

Service methods are defined by adding public methods to a concrete service implementation. All public methods defined by the class automatically become available for remote execution when the service is published, as described in the next section. Note that overloaded methods are not supported; every method name must be unique. 

Method arguments may be any numeric primitive, a boolean primitive, or `String`. Object wrappers for primitive types are also supported. Indexed collection arguments are specified as lists of any supported simple type (e.g. `List<Double>`), and keyed collections are specified as maps (e.g. `Map<String, Integer>`). Map arguments must use `String` values for keys.

Methods may return any numeric or boolean primitive type, one of the following reference types, or `void`:

* `java.lang.Number`
* `java.lang.Boolean`
* `java.lang.String`
* `java.util.List`
* `java.util.Map`

`Map` implementations must use `String` values for keys. Nested structures are supported, but reference cycles are not permitted.

`List` and `Map` types are not required to support random access; iterability is sufficient. Additionally, `List` and `Map` types that implement `java.lang.AutoCloseable` will be automatically closed after their values have been written to the output stream. This allows service implementations to stream response data rather than buffering it in memory before it is written. 

For example, the `org.httprpc.sql.ResultSetAdapter` class wraps an instance of `java.sql.ResultSet` and exposes its contents as a forward-scrolling, auto-closeable list of map values. Closing the list also closes the underlying result set, ensuring that database resources are not leaked. `ResultSetAdapter` is discussed in more detail later.

### Request Metadata
`WebService` provides the following methods that allow an extending class to obtain additional information about the current request:

* `getLocale()` - returns the locale associated with the current request
* `getUserName()` - returns the user name associated with the current request, or `null` if the request was not authenticated
* `getUserRoles()` - returns a set representing the roles the user belongs to, or `null` if the request was not authenticated
* `getAttachments()` - returns the attachments associated with the current request

The values returned by these methods are populated via protected setters, which are called once per request by `RequestDispatcherServlet`. These setters are not meant to be called by application code. However, they can be used to facilitate unit testing of service implementations by simulating a request from an actual client. 

### Examples
The following code demonstrates one possible implementation of the hypothetical math service discussed earlier:

    public class MathService extends WebService {
        // Add a + b
        public double add(double a, double b) {
            return a + b;
        }

        // Add values
        public double addValues(List<Double> values) {
            double total = 0;

            for (double value : values) {
                total += value;
            }

            return total;
        }
    }

A GET request for this URL would invoke the service's `add()` method, producing the number 5 in response:

    /math/add?a=2&b=3
    
Similarly, a GET for the following URL would invoke the `addValues()` method, producing the number 9:

    /math/addValues?values=1&values=3&values=5

## RequestDispatcherServlet Class
HTTP-RPC services are published via the `RequestDispatcherServlet` class. This class is resposible for translating HTTP request parameters to method arguments, invoking the specified method, and serializing the return value to JSON. Note that service classes must be compiled with the `-parameters` flag so their method parameter names are available at runtime.

Java objects are mapped to their JSON equivalents as follows:

* `java.lang.Number` or numeric primitive: number
* `java.lang.Boolean` or boolean primitive: true/false
* `java.lang.String`: string
* `java.util.List`: array
* `java.util.Map`: object

Each servlet instance hosts a single HTTP-RPC service. The name of the service type is passed to the servlet via the "serviceClassName" initialization parameter. For example:

	<servlet>
	    <servlet-name>MathServlet</servlet-name>
	    <servlet-class>org.httprpc.RequestDispatcherServlet</servlet-class>
        <init-param>
            <param-name>serviceClassName</param-name>
            <param-value>com.example.MathService</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>MathServlet</servlet-name>
        <url-pattern>/math/*</url-pattern>
    </servlet-mapping>

A new service instance is created and initialized for each request. `RequestDispatcherServlet` converts the request parameters to the argument types expected by the named method, invokes the method, and writes the return value to the response stream as JSON.

The servlet returns an HTTP 200 status code on successful method completion. If any exception is thrown, HTTP 500 is returned.

Servlet security is provided by the underlying servlet container. See the Java EE documentation for more information.

## BeanAdapter Class
The `BeanAdapter` class allows the contents of a Java Bean object to be returned from a service method. This class implements the `Map` interface and exposes any properties defined by the Bean as entries in the map, allowing custom data types to be serialized to JSON.

For example, the statistical data discussed in the previous section might be represented by the following Bean class:

    public class Statistics {
        private int count = 0;
        private double sum = 0;
        private double average = 0;
    
        public int getCount() {
            return count;
        }
    
        public void setCount(int count) {
            this.count = count;
        }
    
        public double getSum() {
            return sum;
        }
    
        public void setSum(double sum) {
            this.sum = sum;
        }
    
        public double getAverage() {
            return average;
        }
    
        public void setAverage(double average) {
            this.average = average;
        }
    }

Using this class, an implementation of the `getStatistics()` method might look like this:

    public Map<String, Object> getStatistics(List<Double> values) {    
        Statistics statistics = new Statistics();

        int n = values.size();

        statistics.setCount(n);

        for (int i = 0; i < n; i++) {
            statistics.setSum(statistics.getSum() + values.get(i));
        }

        statistics.setAverage(statistics.getSum() / n);

        return new BeanAdapter(statistics);
    }

Although the values are actually stored in the strongly typed `Statistics` object, the adapter makes the data appear as a map, allowing it to be returned to the caller as a JSON object.

Note that, if a property returns a nested Bean type, the property's value will be automatically wrapped in a `BeanAdapter` instance. Additionally, if a property returns a `List` or `Map` type, the value will be wrapped in an adapter of the appropriate type that automatically adapts its sub-elements. 

For example, the `getTree()` method discussed earlier could be implemented using `BeanAdapter` as follows:

    public Map<String, Object> getTree() {
        TreeNode root = new TreeNode();
        ...

        return new BeanAdapter(root);
    }

The `TreeNode` instances returned by the `getChildren()` method will be recursively adapted:

    public class TreeNode {
        public String getName() { ... }    
        public List<TreeNode> getChildren() { ... }
    }

The `BeanAdapter#adapt()` method is used to adapt property values. This method is called internally by `BeanAdapter#get()`, but it can also be used to explicitly adapt list or map values as needed. See the Javadoc for the `BeanAdapter` class for more information.

## ResultSetAdapter Class
The `ResultSetAdapter` class allows the result of a SQL query to be efficiently returned from a service method. This class implements the `List` interface and makes each row in a JDBC result set appear as an instance of `Map`, rendering the data suitable for serialization to JSON. It also implements the `AutoCloseable` interface, to ensure that the underlying result set is closed and database resources are not leaked.

`ResultSetAdapter` is forward-scrolling only; its contents are not accessible via the `get()` and `size()` methods. This allows the contents of a result set to be returned directly to the caller without any intermediate buffering. The caller can simply execute a JDBC query, pass the resulting result set to the `ResultSetAdapter` constructor, and return the adapter instance:

    public List<Map<String, Object>> getData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from some_table");
        
        return new ResultSetAdapter(resultSet);
    }

## Parameters Class
The `Parameters` class provides a means for executing prepared statements using named parameter values rather than indexed arguments. Parameter names are specified by a leading `:` character. For example:

    SELECT * FROM some_table 
    WHERE column_a = :a OR column_b = :b OR column_c = COALESCE(:c, 4.0)
    
The `parse()` method is used to create a `Parameters` instance from a SQL statement. It takes a `java.io.Reader` containing the SQL text as an argument; for example:

    Parameters parameters = Parameters.parse(new StringReader(sql));

The `getSQL()` method of the `Parameters` class returns the parsed SQL in standard JDBC syntax:

    SELECT * FROM some_table 
    WHERE column_a = ? OR column_b = ? OR column_c = COALESCE(?, 4.0)

This value is used to create the actual prepared statement:

    PreparedStatement statement = DriverManager.getConnection(url).prepareStatement(parameters.getSQL());

Parameter values are applied to the statement using the `apply()` method. The first argument to this method is the prepared statement, and the second is a map containing the statement arguments:

    HashMap<String, Object> arguments = new HashMap<>();
    arguments.put("a", "hello");
    arguments.put("b", 3);
    
    parameters.apply(statement, arguments);

Since explicit creation and population of the argument map can be cumbersome, the `WebService` class provides the following static convenience methods to help simplify map creation:

    public static <K> Map<K, ?> mapOf(Map.Entry<K, ?>... entries) { ... }
    public static <K> Map.Entry<K, ?> entry(K key, Object value) { ... }
    
Using the convenience methods, the code that applies the parameter values can be reduced to the following:

    parameters.apply(statement, mapOf(entry("a", "hello"), entry("b", 3)));

Once applied, the statement can be executed:

    return new ResultSetAdapter(statement.executeQuery());    

## IteratorAdapter Class
The `IteratorAdapter` class allows the content of an arbitrary cursor to be efficiently returned from a service method. This class implements the `List` interface and adapts each element produced by the iterator for serialization to JSON, including nested `List` and `Map` structures. Like `ResultSetAdapter`, `IteratorAdapter` implements the `AutoCloseable` interface. If the underlying iterator type also implements `AutoCloseable`, `IteratorAdapter` will ensure that the underlying cursor is closed so that resources are not leaked.

As with `ResultSetAdapter`, `IteratorAdapter` is forward-scrolling only, so its contents are not accessible via the `get()` and `size()` methods. This allows the contents of a cursor to be returned directly to the caller without any intermediate buffering.

`IteratorAdapter` is typically used to serialize result data produced by NoSQL databases.

# Java Client
The Java client implementation of HTTP-RPC enables Java-based applications to consume HTTP-RPC web services. It is distributed as a JAR file that includes the following types, discussed in more detail below:

* _`org.httprpc`_
    * `WebServiceProxy` - invocation proxy for HTTP-RPC services
    * `ResultHandler` - callback interface for handling results
    * `Result` - abstract base class for typed results
    * `Authentication` - interface for authenticating requests
    * `BasicAuthentication` - authentication implementation supporting basic HTTP authentication

The JAR file for the Java client implementation of HTTP-RPC can be downloaded [here](https://github.com/gk-brown/HTTP-RPC/releases). Java 7 or later is required.

## WebServiceProxy Class
The `WebServiceProxy` class acts as a client-side invocation proxy for HTTP-RPC web services. Internally, it uses an instance of `HttpURLConnection` to send and receive data. Requests are submitted via HTTP POST.

`WebServiceProxy` provides a single constructor that takes the following arguments:

* `baseURL` - an instance of `java.net.URL` representing the base URL of the service
* `executorService` - an instance of `java.util.concurrent.ExecutorService` that will be used to execute service requests

The base URL represents the fully qualified name of the service. Method names are appended to this URL during execution. 

The executor service is used to schedule remote method requests. Internally, requests are implemented as a `Callable` that is submitted to the service. See the `ExecutorService` Javadoc for more information.

Remote methods are executed by calling the `invoke()` method:
    
    public <V> Future<V> invoke(String methodName, 
        Map<String, ?> arguments, 
        Map<String, List<URL>> attachments, 
        ResultHandler<V> resultHandler) { ... }

This method takes the following arguments:

* `methodName` - the name of the remote method to invoke
* `arguments` - a map containing the method arguments as key/value pairs
* `attachments` - a map containing any attachments to the method
* `resultHandler` - an instance of `org.httprpc.ResultHandler` that will be invoked upon completion of the remote method

The following convenience methods are also provided:

    public <V> Future<V> invoke(String methodName, 
        ResultHandler<V> resultHandler) { ... }
    
    public <V> Future<V> invoke(String methodName, 
        Map<String, ?> arguments, 
        ResultHandler<V> resultHandler) { ... }

Method arguments can be any numeric type, a boolean, or a string. Indexed collection arguments are specified as lists of any supported simple type (e.g. `List<Double>`), and keyed collections are specified as maps (e.g. `Map<String, Integer>`). Map arguments must use `String` values for keys.

Attachments are specified a map of URL lists. The URLs refer to local resources whose contents will be transmitted along with the method arguments. If provided, they are sent to the server as multipart form data, like an HTML form. See [RFC 2388](https://www.ietf.org/rfc/rfc2388.txt) for more information.

The result handler is called upon completion of the remote method. `ResultHandler` is a functional interface whose single method, `execute()`, is defined as follows:

    public void execute(V result, Exception exception);

On successful completion, the first argument will contain the result of the remote method call. It will be an instance of one of the following types or `null`, depending on the content of the JSON response returned by the server:

* string: `java.lang.String`
* number: `java.lang.Number`
* true/false: `java.lang.Boolean`
* array: `java.util.List`
* object: `java.util.Map`

The second argument will always be `null` in this case. If an error occurs, the first argument will be `null` and the second will contain an exception representing the error that occurred.

All variants of the `invoke()` method return an instance of `java.util.concurrent.Future` representing the invocation request. This object allows a caller to cancel an outstanding request as well as obtain information about a request that has completed.

### Argument Map Creation
Since explicit creation and population of the argument map can be cumbersome, `WebServiceProxy` provides the following static convenience methods to help simplify map creation:

    public static <K> Map<K, ?> mapOf(Map.Entry<K, ?>... entries) { ... }
    public static <K> Map.Entry<K, ?> entry(K key, Object value) { ... }
    
Using these convenience methods, argument map creation can be reduced from this:

    HashMap<String, Object> arguments = new HashMap<>();
    arguments.put("a", 2);
    arguments.put("b", 4);
    
to this:

    Map<String, Object> arguments = mapOf(entry("a", 2), entry("b", 4));
    
A complete example using `WebServiceProxy#invoke()` is shown later.

### Multi-Threading Considerations
By default, a result handler is called on the thread that executed the remote request, which in most cases will be a background thread. However, user interface toolkits generally require updates to be performed on the main thread. As a result, handlers typically need to "post" a message back to the UI thread in order to update the application's state. For example, a Swing application might call `SwingUtilities#invokeAndWait()`, whereas an Android application might call `Activity#runOnUiThread()` or `Handler#post()`.

While this can be done in the result handler itself, `WebServiceProxy` provides a more convenient alternative. The `setResultDispatcher()` method allows an application to specify an instance of `java.util.concurrent.Executor` that will be used to perform all result handler notifications. This is a static method that only needs to be called once at application startup.

For example, the following Android-specific code ensures that all result handlers will be executed on the main UI thread:

    WebServiceProxy.setResultDispatcher(new Executor() {
        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    });

Similar dispatchers can be configured for other Java UI toolkits such as Swing, JavaFX, and SWT. Command line applications can generally use the default dispatcher, which simply performs result handler notifications on the current thread.

## Result Class
`Result` is an abstract base class for typed results. Using this class, applications can easily map untyped object data returned by a service method to typed values. It provides the following constructor that is used to populate Java Bean property values from map entries:

    public Result(Map<String, Object> properties) { ... }
    
For example, the following Java class might be used to provide a typed version of the statistical data returned by the `getStatistics()` method discussed earlier:

    public class Statistics extends Result {
        private int count;
        private double sum;
        private double average;

        public Statistics(Map<String, Object> properties) {
            super(properties);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getSum() {
            return sum;
        }

        public void setSum(double sum) {
            this.sum = sum;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }
    }

The map data returned by `getStatistics()` can be converted to a `Statistics` instance as follows:

    serviceProxy.invoke("getStatistics", (Map<String, Object> result, Exception exception) -> {
        Statistics statistics = new Statistics(result);

        // Prints 3, 9.0, and 3.0
        System.out.println(statistics.getCount());
        System.out.println(statistics.getSum());
        System.out.println(statistics.getAverage());
    });

## Authentication
Although it is possible to use the `java.net.Authenticator` class to authenticate service requests, this class can be difficult to work with, especially when dealing with multiple concurrent requests or authenticating to multiple services with different credentials. It also requires an unnecessary round trip to the server if a user's credentials are already known up front, as is often the case.

HTTP-RPC provides an additional authentication mechanism that can be specified on a per-proxy basis. The `org.httprpc.Authentication` interface defines a single method that is used to authenticate each request submitted by a proxy instance:

    public interface Authentication {
        public void authenticate(HttpURLConnection connection);
    }

Authentication providers are associated with a proxy instance via the `setAuthentication()` method of the `WebServiceProxy` class. For example, the following code associates an instance of `org.httprpc.BasicAuthentication` with a service proxy:

    serviceProxy.setAuthentication(new BasicAuthentication("username", "password"));

The `BasicAuthentication` class is provided by the HTTP-RPC Java client library. Applications may provide custom implementations of the `Authentication` interface to support other authentication schemes.

## Examples
The following code snippet demonstrates how `WebServiceProxy` can be used to invoke the methods of the hypothetical math service discussed earlier. It first creates an instance of the `WebServiceProxy` class and configures it with a pool of ten threads for executing requests. It then invokes the `add()` method of the service, passing a value of 2 for "a" and 4 for "b". Finally, it executes the `addValues()` method, passing the values 1, 2, 3, and 4 as arguments:

    // Create service
    URL baseURL = new URL("https://localhost:8443/httprpc-test-server/test/");
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    WebServiceProxy serviceProxy = new WebServiceProxy(baseURL, executorService);

    // Add a + b
    serviceProxy.invoke("add", mapOf(entry("a", 2), entry("b", 4)), new ResultHandler<Number>() {
        @Override
        public void execute(Number result, Exception exception) {
            // result is 6
        }
    });
    
    // Add values
    serviceProxy.invoke("add", mapOf(entry("values", Arrays.asList(1, 2, 3, 4))), new ResultHandler<Number>() {
        @Override
        public void execute(Number result, Exception exception) {
            // result is 10
        }
    });

Note that, in Java 8 or later, lambda expressions can be used instead of anonymous classes to implement result handlers, reducing the code for invoking the remote methods to the following:

    // Add a + b
    serviceProxy.invoke("add", mapOf(entry("a", 2), entry("b", 4)), (result, exception) -> {
        // result is 6
    });

    // Add values
    serviceProxy.invoke("addValues", mapOf(entry("values", Arrays.asList(1, 2, 3, 4))), (result, exception) -> {
        // result is 10
    });

# Objective-C/Swift Client
The Objective-C/Swift client implementation of HTTP-RPC enables iOS applications to consume HTTP-RPC services. It is delivered as a modular framework that includes the following types, discussed in more detail below:

* `WSWebServiceProxy` - invocation proxy for HTTP-RPC services
* `WSAuthentication` - interface for authenticating requests
* `WSBasicAuthentication` - authentication implementation supporting basic HTTP authentication

The framework for the Objective-C/Swift client can be downloaded [here](https://github.com/gk-brown/HTTP-RPC/releases). It is also available via [CocoaPods](https://cocoapods.org/pods/HTTP-RPC). iOS 8 or later is required.

## WSWebServiceProxy Class
The `WSWebServiceProxy` class serves as an invocation proxy for HTTP-RPC services. Internally, it uses an instance of `NSURLSession` to issue HTTP requests, which are submitted via HTTP POST. It uses the `NSJSONSerialization` class to deserialize response content.

Service proxies are initialized via the `initWithSession:baseURL:` method, which takes an `NSURLSession` instance and the service's base URL as arguments. Method names are appended to this URL during method execution.

Remote methods are executed by calling the `invoke:withArguments:attachments:resultHandler:` method:
    
    - (NSURLSessionDataTask *)invoke:(NSString *)methodName
        withArguments:(NSDictionary *)arguments
        attachments:(NSDictionary *)attachments
        resultHandler:(void (^)(id, NSError *))resultHandler;

This method takes the following arguments:

* `methodName` - the name of the remote method to invoke
* `arguments` - a dictionary containing the method arguments as key/value pairs
* `attachments` - a dictionary containing any attachments to the method
* `resultHandler` - a callback that will be invoked upon completion of the method

The following convenience methods are also provided:

    - (NSURLSessionDataTask *)invoke:(NSString *)methodName
        resultHandler:(void (^)(id, NSError *))resultHandler;
    
    - (NSURLSessionDataTask *)invoke:(NSString *)methodName
        withArguments:(NSDictionary *)arguments
        resultHandler:(void (^)(id, NSError *))resultHandler;

Method arguments can be any numeric type, a boolean, or a string. Indexed collection arguments are specified as arrays of any supported simple type (e.g. `[Double]`), and keyed collections are specified as dictionaries (e.g. `[String: Int]`). Dictionary arguments must use `String` values for keys.

Attachments are specified a dictionary of URL arrays. The URLs refer to local resources whose contents will be transmitted along with the method arguments. If provided, they are sent to the server as multipart form data, like an HTML form. See [RFC 2388](https://www.ietf.org/rfc/rfc2388.txt) for more information.

The result handler callback is called upon completion of the remote method. The callback takes two arguments: a result object and an error object. If the remote method completes successfully, the first argument contains the value returned by the method, or `nil` if the method does not return a value. If the method call fails, the second argument will be populated with an instance of `NSError` describing the error that occurred.

All three variants of the `invoke` method return an instance of `NSURLSessionDataTask` representing the invocation request. This allows an application to cancel a task, if necessary.

Although requests are typically processed on a background thread, result handlers are called on the same operation queue that initially invoked the service method. This is typically the application's main queue, which allows result handlers to update the application's user interface directly, rather than posting a separate update operation to the main queue.

## Authentication
Although it is possible to use the `URLSession:task:didReceiveChallenge:completionHandler:` method of the `NSURLSessionDataDelegate` protocol to authenticate service requests, this method requires an unnecessary round trip to the server if a user's credentials are already known up front, as is often the case.

HTTP-RPC provides an additional authentication mechanism that can be specified on a per-proxy basis. The `WSAuthentication` protocol defines a single method that is used to authenticate each request submitted by a proxy instance:

    - (void)authenticate:(NSMutableURLRequest *)request;

Authentication providers are associated with a proxy instance via the `authentication` property of the `WSWebServiceProxy` class. For example, the following code associates an instance of `WSBasicAuthentication` with a service proxy:

    serviceProxy.authentication = WSBasicAuthentication(username: "username", password: "password")

The `WSBasicAuthentication` class is provided by the HTTP-RPC framework. Applications may provide custom implementations of the `WSAuthentication` protocol to support other authentication schemes.

## Examples
The following code snippet demonstrates how `WSWebServiceProxy` can be used to invoke the methods of the hypothetical math service. It first creates an instance of the `WSWebServiceProxy` class backed by a default URL session and a delegate queue supporting ten concurrent operations. It then invokes the `add()` method of the service, passing a value of 2 for "a" and 4 for "b". Finally, it executes the `addValues()` method, passing the values 1, 2, 3, and 4 as arguments:

    // Configure session
    let configuration = NSURLSessionConfiguration.defaultSessionConfiguration()
    configuration.requestCachePolicy = NSURLRequestCachePolicy.ReloadIgnoringLocalAndRemoteCacheData

    let delegateQueue = NSOperationQueue()
    delegateQueue.maxConcurrentOperationCount = 10

    let session = NSURLSession(configuration: configuration, delegate: self, delegateQueue: delegateQueue)

    // Initialize service proxy and invoke methods
    let baseURL = NSURL(string: "https://localhost:8443/httprpc-test-server/test/")

    let serviceProxy = WSWebServiceProxy(session: session, baseURL: baseURL!)
    
    // Add a + b
    serviceProxy.invoke("add", withArguments: ["a": 2, "b": 4]) {(result, error) in
        // result is 6
    }

    // Add values
    serviceProxy.invoke("addValues", withArguments: ["values": [1, 2, 3, 4]]) {(result, error) in
        // result is 10
    }

# JavaScript Client
The JavaScript client implementation of HTTP-RPC enables browser-based applications to consume HTTP-RPC services. It is delivered as source code file and defines a single `WebServiceProxy` class, which is discussed in more detail below. 

The source code for the JavaScript client can be downloaded [here](https://github.com/gk-brown/HTTP-RPC/releases).

## WebServiceProxy Class
The `WebServiceProxy` class serves as an invocation proxy for HTTP-RPC services. Internally, it uses an instance of `XMLHttpRequest` to communicate with the server, and uses `JSON.parse()` to convert the response to an object. Requests are submitted via HTTP POST. 

Service proxies are initialized via the `WebServiceProxy` constructor, which takes a single `baseURL` argument representing the path to the service. Method names are appended to this URL during method execution.

Remote methods are invoked by calling either `invoke()` or `invokeWithArguments()` on the service proxy. The first version is a convenience method for calling remote methods that don't take any arguments. The second takes an object containing the set of argument values to be passed to the remote method. The first method delegates to the second, passing an empty argument object.

Method arguments can be any numeric type, a boolean, or a string. Indexed collection arguments are specified as arrays of any supported simple type (e.g. `[2.0, 4.5, 3.1]`), and keyed collections are specified as objects (e.g. `{"a":1, "b":2, "c":3}`).

Both invocation methods take a result handler as the final argument. The result handler is a callback function that is invoked upon successful completion of the remote method, as well as if the method call fails. The callback takes two arguments: a result object and an error object. If the remote method completes successfully, the first argument contains the value returned by the method, or `null` if the method does not return a value. If the method call fails, the second argument will contain the HTTP status code corresponding to the error that occurred.

Both methods return the `XMLHttpRequest` instance used to execute the remote call. This allows an application to cancel a request, if necessary.

## Examples
The following code snippet demonstrates how `WebServiceProxy` can be used to invoke the methods of the hypothetical math service. It first creates an instance of the `WebServiceProxy` class that points to the base service URL. It then invokes the `add()` method of the service, passing a value of 2 for "a" and 4 for "b". Finally, it executes the `addValues()` method, passing the values 1, 2, 3, and 4 as arguments:

    // Initialize service proxy and invoke methods
    var serviceProxy = new WebServiceProxy("/httprpc-test-server/test");

    // Add
    serviceProxy.invokeWithArguments("add", {a:4, b:2}, function(result, error) {
        // result is 6
    });

    // Add values
    serviceProxy.invokeWithArguments("addValues", {values:[1, 2, 3, 4]}, function(result, error) {
        // result is 10
    });

# More Information
For additional information and examples, see the [the wiki](https://github.com/gk-brown/HTTP-RPC/wiki).
