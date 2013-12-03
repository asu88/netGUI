/************************************************************
 * Proyecto: Graphics user interface for NetKit
 * Autor: Santiago Carot Nemesio
 * Dirigido por: Pedro de las Heras Quir� (GSyC)
 * Universidad Rey Juan Carlos 2005
 ************************************************************/

/***********************************************************
 * Tipo de conexión utilizada para conectar dos routers
 * directamente.
 ***********************************************************/
public class NKDirectConection extends NKConection
{
        public static final long serialVersionUID = 1L;
	private String netName;

	public NKDirectConection (NKNode node1, NKNode node2, String netName)
	{
		super (node1,node2);
		this.netName = netName;
	}

	public String getNetName ()
	{
		return netName;
	}
}