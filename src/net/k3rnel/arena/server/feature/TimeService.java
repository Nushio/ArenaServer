/**
 *  Copyright (C) 2012 K3RNEL Developer Team
 *  
 *  This file is part of Distro Wars (Server).
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.k3rnel.arena.server.feature;

import java.util.Calendar;

/**
 * Handles game time
 * @author shadowkanji
 *
 */
public class TimeService implements Runnable {
	private boolean m_isRunning;
	private Thread m_thread;
	private static int m_hour;
	private static int m_minutes;
	private static int m_day = 0;
	
	/**
	 * Default constructor
	 */
	public TimeService() {
		/*
		 * Generate random weather
		 */
		
		m_thread = new Thread(this);
	}
	
	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		try {
			/*
			 * Parses time from a common server.
			 * The webpage should just have text (no html tags) in the form:
			 * DAY HOUR MINUTES
			 * where day is a number from 0 - 6
			 */
//			URL url = new URL("");
//			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//			StringTokenizer s = new StringTokenizer(in.readLine());
//			m_day = Integer.parseInt(s.nextToken());
//			m_hour = Integer.parseInt(s.nextToken());
//			m_minutes = Integer.parseInt(s.nextToken());
//			in.close();
			Calendar cal = Calendar.getInstance();
			m_hour = cal.get(Calendar.HOUR_OF_DAY);
			m_minutes = cal.get(Calendar.MINUTE);
			m_day = cal.get(Calendar.DAY_OF_WEEK);
		} catch (Exception e) {
			System.out.println("ERROR: Cannot reach time server, reverting to local time");
			/* Can't reach website, base time on local */
			Calendar cal = Calendar.getInstance();
			m_hour = cal.get(Calendar.HOUR_OF_DAY);
			m_minutes = cal.get(Calendar.MINUTE);
            m_day = cal.get(Calendar.DAY_OF_WEEK);
		}
		while(m_isRunning) {
			//Update the time. Time moves 4 times faster.
			m_minutes = m_minutes == 59 ? 0 : m_minutes + 1;
			if(m_minutes == 0) {
				if(m_hour == 23) {
					incrementDay();
					m_hour = 0;
				} else {
					m_hour += 1;
				}
			}
				m_hour = m_hour == 23 ? 0 : m_hour + 1;
			try {
				Thread.sleep(60000);
			} catch (Exception e) {}
		}
		System.out.println("INFO: Time Service stopped");
	}
	
	/**
	 * Increments the day on the server
	 */
	public void incrementDay() {
		m_day = m_day == 6 ? 0 : m_day + 1;
	}
	
	/**
	 * Starts this Time Service
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
		System.out.println("INFO: Time Service started");
	}

	/**
	 * Stops this Time Service
	 */
	public void stop() {
		m_isRunning = false;
	}
	
	/**
	 * Returns a string representation of the current time, e.g. 1201
	 * @return
	 */
	public static String getTime() {
		return "" + (m_hour < 10 ? "0" + m_hour : m_hour) + (m_minutes < 10 ? "0" + m_minutes : m_minutes);
	}
	
	/**
	 * Returns the hour of the day (game time)
	 * @return
	 */
	public static int getHourOfDay() {
		return m_hour;
	}
	
	/**
	 * Returns the current minute (game time)
	 * @return
	 */
	public static int getMinuteOfDay() {
		return m_minutes;
	}
	
}
