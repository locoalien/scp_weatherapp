package com.sap.hana.cloud.samples.weather.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.sap.core.connectivity.api.http.HttpDestination;
/**
 * Permite consumir un servicio web Externo
 * @author Santiago Gonzalez
 *
 */

@Path("/weather")
@Produces({ MediaType.APPLICATION_JSON })
public class WeatherService 
{
	
	private static final int COPY_CONTENT_BUFFER_SIZE = 1024;

	@GET
	@Path("/")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getWeatherInformation(@QueryParam(value = "id") String id, @QueryParam(value = "q") String q)
	{
	    HttpClient httpClient = null;
	    HttpGet httpGet = null;
	    
	    String msgBody = null;
	    
        try 
        {
            // Get HTTP destino (destination)
            Context ctx = new InitialContext();
            
            HttpDestination destination = (HttpDestination) ctx.lookup("java:comp/env/" +  "openweathermap-destination");

            // Create HTTP cliente
            httpClient = destination.createHttpClient();
            
            final String baseURL = destination.getURI().toString();
            
            String destinationURL = null;
            
            if (id != null && id.trim().length() > 0) 
            {
            	destinationURL = MessageFormat.format("{0}&id={1}&units=metric", baseURL, id);
            }
            else
            {
            	destinationURL = MessageFormat.format("{0}&q={1}&units=metric", baseURL, q);
            }
            
            // Ejecutar HTTP request
            httpGet = new HttpGet(destinationURL);
            
            HttpResponse httpResponse = httpClient.execute(httpGet);

            // Obtener respuesta estado de codigo
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            
            // Copiar el contenido de la respuesta entrante a la respuesta saliente
            HttpEntity entity = null;
            
            if (httpResponse != null)
            {
            	entity = httpResponse.getEntity();
            }
            
            msgBody = getResponseBodyasString(entity);
            
            if (statusCode == HttpServletResponse.SC_OK) 
            {
                return Response.ok(msgBody).build();
            }
            else
            {
            	return Response.status(statusCode).entity(msgBody).build();
            }

        } 
        catch (RuntimeException e) 
        {
        	// En caso de una excepción inesperada abortamos la petición HTTP
        	// para cerrar inmediatamente la conexión subyacente.
            if (httpGet != null)
            {
            	httpGet.abort();
            }
        	
            // Error inesperado en la ejecucion
            String errorMessage = "'Ocurrio un problema inesperado!' : "
                    + e.getMessage()
                    + ". Ver el log para mas detalle.";
            
            msgBody = errorMessage;
        } 
        catch (NamingException e) 
        {
            //Error de búsqueda de destino
            String errorMessage = "La búsqueda del destino falló por la siguiente razon: "
                    + e.getMessage()
                    + ". Ver"
                    + "el log para mas detalle. Sugerencia: asegúrese de tener el destino "
                    + "[openweathermap-destination]" + " Configurado.";
            
            msgBody = errorMessage;
        } 
        catch (Exception e) 
        {
            // Error de la operación de conectividad
            String errorMessage = "La operación de conectividad falló por la siguiente razon: "
                    + e.getMessage()
                    + ". Ver "
                    + "el log para mas detalle. Sugerencia: asegúrese de tener configurado un proxy HTTP en su "
                    + "local Eclipse environment en caso de que su entorno utilice "
                    + "un HTTP proxy para la salida de Internet";
            
            msgBody = errorMessage;
        } 
        finally 
        {
        	// Cuando la instancia de HttpClient ya no sea necesaria, apague el gestor de conexiones para garantizar la inmediata
        	// desasignación de todos los recursos del sistema
            if (httpClient != null) 
            {
                httpClient.getConnectionManager().shutdown();
            }
        }
        
        // Si retorna este error, algo esta mal configurado
        return Response.serverError().build();
    }
	
	/**
	 * Extrae el cuerpo de la respuesta de la {@link HttpEntity} especificada y la devuelve como una cadena codificada en UTF-8.
	 * 
	 * @param entity {@link HttpEntity} para extraer el cuerpo del mensaje de
	 * @return La representación de cadena codificada UTF-8 del cuerpo del mensaje
	 * @throws 
	 */
	static String getResponseBodyasString(HttpEntity entity) throws Exception
	{
		String retVal = null;
		
		if (entity != null) 
        {
            InputStream instream = entity.getContent();
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            
            try 
            {
                byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
                int len;
                while ((len = instream.read(buffer)) != -1) 
                {
                	outstream.write(buffer, 0, len);
                }
            } 
            catch (IOException e) 
            {
            	// En caso de una excepción IOException, la conexión se liberará
                // volver al gestor de conexiones automáticamente
                throw e;
            } 
            finally 
            {
                // Cerrar el flujo de entrada activará la liberación de la conexión
                try 
                {
                    instream.close();
                } 
                catch (Exception e) 
                {
               
                }
            }
            
            retVal = outstream.toString("UTF-8");
        }
		
		return retVal;
	}
}
	

