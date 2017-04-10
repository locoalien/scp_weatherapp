package com.sap.hana.cloud.samples.weather.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

/**
 * Base para todos los dominios de los objetos
 */
@MappedSuperclass
//@Multitenant
//@TenantDiscriminatorColumn(name = "TENANT_ID", contextProperty="tenant.id")
public abstract class BaseObject 
{
	/**
	 * Global y unico Id del objeto
	 */
	@Id
    @Column(name="GUID", length = 36)
    private String guid = UUID.randomUUID().toString();

	/**
	 * {@link Date} Fecha de reacion.
	 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATION_DATE", updatable = false)
    private Date createdAt = null;
 
    /**
	 * {@link Date} Fecha de modificacion.
	 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="MODIFICATION_DATE")
    private Date lastModifiedAt = null;

    /**
     * ID del usuario que creó el objeto.
     */
    @Column(name="CREATED_BY", updatable = false, length = 20)
    private String createdBy = null;
    
    /**
     * ID del usuario que fue el último en modificar el objeto.
     */
    @Column(name="MODIFIED_BY", length = 20)
    private String lastModifiedBy = null;
        
    /**
     * Devolución de eventos de ciclo de vida, que establece automáticamente la fecha de última modificación.  
     */
    @PreUpdate
    protected void updateAuditInformation() 
    {
        lastModifiedAt = new Date();
        
        // TODO - obtain currently logged-on user
    }

    /**
     * Devolución de llamada de evento de ciclo de vida, que crea automáticamente un identificador 
     * único para el objeto y rellena su información de auditoría.  
     */
    @PrePersist
    protected void generateAuditInformation() 
    {   
        final Date now = new Date();
        
        createdAt = now;
        lastModifiedAt = now;
        
        // TODO - obtain currently logged-on user
    }

    
    public String getGuid()
	{
		return this.guid;
	}

	public void setGuid(String guid)
	{
		this.guid = guid;
	}
    
	public Date getCreatedAt()
	{
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt)
	{
		this.createdAt = createdAt;
	}

	public Date getLastModifiedAt()
	{
		return this.lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt)
	{
		this.lastModifiedAt = lastModifiedAt;
	}

	public String getCreatedBy()
	{
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy()
	{
		return this.lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy)
	{
		this.lastModifiedBy = lastModifiedBy;
	}
}
