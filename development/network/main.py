from appConnectLayer import *
from server_esp_api import *
import server_configuration
# ligação à app
# camada de comunicação da bd à app a e à lock
# ligação ao lock

if __name__ == "__main__":
    lockWebServer = HTTPServer((server_configuration.hostName, server_configuration.espApiPort), MyServer)
    print("Esp api opened at http://%s:%s" % (server_configuration.hostName, server_configuration.espApiPort))
    lockThread = threading.Thread(target=lockWebServer.serve_forever)

    api_control_thread = threading.Thread(
        target=output_control, args=())
    api_control_thread.daemon = True
    api_control_thread.start()
    appThread = threading.Thread(target=app.run(host=server_configuration.hostName, port=server_configuration.appApiPort, ssl_context=('cert.pem', 'key.pem'), debug = True))
    appThread.start()

