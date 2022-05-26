package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO meteoDAO;
	List <List<Rilevamento>> sequenzaCorretta = new ArrayList<List<Rilevamento>>();
	List<Rilevamento> allRilevamenti = new ArrayList<Rilevamento>();
	List<Rilevamento> rilevamentiMese = new ArrayList<Rilevamento>();
	List<Rilevamento> rilevamentiGiorno = new ArrayList<Rilevamento>();
	int costoMin = 1000000;

	public Model() 
	{
		this.meteoDAO = new MeteoDAO();
	}
	
	public List<Rilevamento> getAllRilevamenti()
	{
		return this.meteoDAO.getAllRilevamenti();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) 
	{
		allRilevamenti = new ArrayList<Rilevamento>();
		allRilevamenti = getAllRilevamenti();
		rilevamentiMese = new ArrayList<Rilevamento>();
		rilevamentiMese = getRilevamentiMese(mese);
		int u_T = 0;
		int Torino = 0;
		int u_M = 0;
		int Milano = 0;
		int u_G = 0;
		int Genova = 0;
		for(Rilevamento r : rilevamentiMese)
		{
			if(r.getLocalita().compareTo("Torino")==0)
				{
					u_T+=r.getUmidita();
					Torino++;
				}

			if(r.getLocalita().compareTo("Milano")==0)
			{
				u_M+=r.getUmidita();
				Milano++;
			}
			
			if(r.getLocalita().compareTo("Genova")==0)
			{
				u_G+=r.getUmidita();
				Genova++;
			}	
		}
		u_T = u_T/Torino;
		u_M = u_M/Milano;
		u_G = u_G/Genova;
		String result = "Umidità:\nTorino: " + u_T + "\nMilano: " + u_M + "\nGenova: " + u_G;
		return result;
	}
	
	public List<Rilevamento> getRilevamentiMese(int mese)
	{
		for(int j=0; j<allRilevamenti.size(); j++)
		//	for(int j=0; j<10; j++)
		{
			if(allRilevamenti.get(j).getData().getMonthValue()==mese)
				rilevamentiMese.add(allRilevamenti.get(j));
		}
		return rilevamentiMese;
	}

	public List<Rilevamento> getRilevamentiGiorno(int livello, List<Rilevamento> rilevamentiMese)
	{
		for(int k=0; k<rilevamentiMese.size(); k++)
		{
			if(rilevamentiMese.get(k).getData().getDayOfMonth()==livello)
				rilevamentiGiorno.add(rilevamentiMese.get(k));
		}
		return rilevamentiGiorno;
	}
	
	
	// of course you can change the String output with what you think works best
	List<Rilevamento> parz = new ArrayList<Rilevamento>();
	
	public String trovaSequenza(int mese) 
	{
		allRilevamenti = getAllRilevamenti();
		rilevamentiMese = getRilevamentiMese(mese);
		ricorsiva(parz, 1, mese);
		int z=0;
		int indiceMin=0;
		
// trovare la sequenza con costo minimo
		for(List<Rilevamento> list : sequenzaCorretta)
		{
			int costo = list.get(0).getUmidita();
			
			for (int x=1; x<list.size(); x++)
			{
				costo+=list.get(x).getUmidita();
				
				if(list.get(x).getLocalita().compareTo(list.get(x-1).getLocalita())!=0)
					costo+=100;
			}
			
			if(costo<costoMin)
			{
				indiceMin = z;
				costoMin=costo;
			}
			z++;
		}
		return sequenzaCorretta.get(indiceMin).toString();
	}
	
	public void ricorsiva(List<Rilevamento> parziale, int livello, int mese)
	{
		int c_T=0;
		int c_M=0;
		int c_G=0;
		
	// controllo 6 giorni max
		for(Rilevamento r : parziale)
		{
			if(r.getLocalita().compareTo("Torino")==0)
				c_T++;
			if(r.getLocalita().compareTo("Milano")==0)
				c_M++;
			if(r.getLocalita().compareTo("Genova")==0)
				c_G++;
		}
		
		if(c_T>6 || c_M>6 || c_G>6)
			return;
		
	// controllo 3 giorni consecutivi
		int consecutivi=1;
		
		for(int i=1; i<parziale.size(); i++)
		{
			if(parziale.get(i).getLocalita().compareTo(parziale.get(i-1).getLocalita())==0)
				consecutivi++;
			
			else if(parziale.get(i).getLocalita().compareTo(parziale.get(i-1).getLocalita())!=0 && consecutivi<3)
				return;
			
			else if(parziale.get(i).getLocalita().compareTo(parziale.get(i-1).getLocalita())==0 && consecutivi>=3)
				consecutivi=1;
		}
		
// casi terminali
		if(livello==16)
			sequenzaCorretta.add(new ArrayList<Rilevamento>(parziale));
		
// caso intermedio
		rilevamentiGiorno = new ArrayList<Rilevamento>();
		rilevamentiGiorno = getRilevamentiGiorno(livello, rilevamentiMese);
		for(Rilevamento ril : rilevamentiGiorno)
		{
			parziale.add(ril);
			ricorsiva(parziale, livello+1, mese);
			parziale.remove(parziale.size()-1);
		}
	}

}
