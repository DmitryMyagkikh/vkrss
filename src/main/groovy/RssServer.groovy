import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet.Context

class RssServer {

    public static void main(String[] args) {

        CliBuilder cli = new CliBuilder(usage: '-port 9090')
        cli.port(args: 1, argName: 'port', 'Jetty port (9090)')
        OptionAccessor opts = cli.parse(args)
        if (!opts.port || !opts.port.isNumber()) {
            cli.usage()
            System.exit(1)
        }


        int port = opts.port as int
        def jetty = new Server(port)

        def context = new Context(jetty, '/', Context.SESSIONS)  // Allow sessions.
//        context.resourceBase = './src/main/groovy'  // Look in current dir for Groovy scripts.
        context.setAttribute('version', '1.0')  // Set an context attribute.
        context.addServlet(RssServlet, '/')

        jetty.start()
    }
}
