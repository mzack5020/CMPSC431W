# CMPSC 431W - Pennsylvania State University

This application was designed in accordance to the guidelines and regulations provided by the CMPSC 431W Capstone course offered at Pennslyvania State University - University Park Campus.

# eLancer

eLancer is the modern way of obtaining obscure, "not-your-everyday" services. Instead of having to go through phone books or other more cumbersome services, eLancer provides a modern, more convenient approach to discovering businesses for your need. 

Styled like eBay, customers can post services needed. These services can range from plumbing to designing websites. Once a service is posted, contractors can monitor and find the services they want to take on and bid on them, just like an auction on eBay. 

The customer then picks the contractor they want; the contractors also have ratings like Angie's List which makes them accountable and which gives customer's ease of mind when picking a contractor.

This application was generated using JHipster 3.10.0, you can find documentation and help at [https://jhipster.github.io/documentation-archive/v3.10.0](https://jhipster.github.io/documentation-archive/v3.10.0).

# Technology stack on the client side
Single Web page application:

Responsive Web Design
HTML5 Boilerplate
Twitter Bootstrap
AngularJS
Compatible with IE9+ and modern browsers
Full internationalization support with Angular Translate
Optional Sass support for CSS design
Optional WebSocket support with Spring Websocket
With the great Yeoman development workflow:

Easy installation of new JavaScript libraries with Bower
Build, optimization and live reload with Gulp.js
Testing with Karma and PhantomJS
And what if a single Web page application isn’t enough for your needs?

Support for the Thymeleaf template engine, to generate Web pages on the server side

# Technology stack on the server side

A complete Spring application:

Spring Boot for easy application configuration
Maven or Gradle configuration for building, testing and running the application
“development” and “production” profiles (both for Maven and Gradle)
Spring Security
Spring MVC REST + Jackson
Optional WebSocket support with Spring Websocket
Spring Data JPA + Bean Validation
Database updates with Liquibase
Elasticsearch support if you want to have search capabilities on top of your database
MongoDB support if you’d rather use a document-oriented NoSQL database instead of JPA
Cassandra support if you’d rather use a column-oriented NoSQL database instead of JPA
Kafka support if you want to use a publish-subscribe messaging system
Ready to go into production:

Monitoring with Metrics
Caching with ehcache (local cache) or hazelcast (distributed cache)
Optional HTTP session clustering with hazelcast
Optimized static resources (gzip filter, HTTP cache headers)
Log management with Logback, configurable at runtime
Connection pooling with HikariCP for optimum performance
Builds a standard WAR file or an executable JAR file
Support for all major cloud providers: AWS, CloudFoundry, Heroku, Kubernetes, Docker…

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:
1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools (like
[Bower][] and [BrowserSync][]). You will only need to run this command when dependencies change in package.json.

    npm install

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

    npm install -g gulp-cli

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    gulp

Bower is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in `bower.json`. You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

## Building for production

To optimize the eLancer application for production, run:

    ./mvnw -Pprod clean package

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar target/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./mvnw clean test

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in `src/test/javascript/` and can be run with:

    gulp test



For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the `src/main/docker` folder to launch required third party services.
For example, to start a mysql database in a docker container, run:

    docker-compose -f src/main/docker/mysql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/mysql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./mvnw package -Pprod docker:build

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`yo jhipster:docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

[JHipster Homepage and latest documentation]: https://jhipster.github.io
[JHipster 3.10.0 archive]: https://jhipster.github.io/documentation-archive/v3.10.0

[Using JHipster in development]: https://jhipster.github.io/documentation-archive/v3.10.0/development/
[Using Docker and Docker-Compose]: https://jhipster.github.io/documentation-archive/v3.10.0/docker-compose
[Using JHipster in production]: https://jhipster.github.io/documentation-archive/v3.10.0/production/
[Running tests page]: https://jhipster.github.io/documentation-archive/v3.10.0/running-tests/


[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
