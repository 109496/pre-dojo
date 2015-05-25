/**
 * 
 */
package com.rankfps.source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.rankfps.bean.Gun;
import com.rankfps.bean.Match;
import com.rankfps.bean.Player;

/**
 * @author Felipe
 *
 */
public class MainClass {
	
	private static String LOG_PATH = "C:/Users/Felipe/Documents/log.txt";
	private static String OUTPUT_PATH = "C:/Users/Felipe/Documents/output.txt";
	private static String LINE_SEPARATOR = "line.separator";
	
	static final Comparator<Player> ORDER_BY_KILLS = new Comparator<Player>() {
		public int compare(Player p1, Player p2) {
			if(p1.getKills() == null) {
				return 1;
			}
			if(p2.getKills() == null) {
				return -1;
			}
			Integer p1Kills = p1.getKills().size();
			Integer p2Kills = p2.getKills().size();
			return p2Kills.compareTo(p1Kills);
		}
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Match> matches = readInputFile();
		for(Match match : matches) {
			calculatePlayersStats(match.getPlayers());
			Collections.sort(match.getPlayers(), ORDER_BY_KILLS);
		}
		createOutputText(matches);
	}
	
	private static void createOutputText(List<Match> matches) {
		if(matches != null) {
			StringBuilder result = new StringBuilder(150);
			String newLine = System.getProperty(LINE_SEPARATOR);
			result.append("Player: kill/death/streak" + newLine + newLine);
			
			for(Match match : matches) {
				result.append("Match: " + match.getId() + newLine);
				for(Player player : match.getPlayers()) {
					result.append(player.getNickname() + ": "); 
					result.append(player.getKills() != null ? player.getKills().size() : "0");
					result.append("/");
					result.append(player.getDeaths() != null ? player.getDeaths().size() : "0");
					result.append("/");
					result.append(player.getStreak());
					if(player.getDeaths() == null || player.getDeaths().size() == 0) {
						result.append(" / FLAWLESS");
					}
					if(player.getKills() != null && kill5TimesIn1Minute(player.getKills())) {
						result.append(" / KILLING SPREE");
					}
					if(player.getBetterGun() != null) {
						result.append(" / Better Gun: " + player.getBetterGun().getName());
					}
					result.append(newLine);
				}
			}
			System.out.println(result.toString());
			writeToOutputFile(result.toString());
		}
		
	}

	private static Boolean kill5TimesIn1Minute(List<Date> kills) {
		if(kills.size() < 5) {
			return Boolean.FALSE;
		} else {
			for(int i=0; i < kills.size(); i++) {
				if(kills.size() > i+4) {
					if(kills.get(i+4).before(sumMinutes(kills.get(i), 1))) {
						return Boolean.TRUE;
					}
				} else {
					return Boolean.FALSE;
				}
			}
		}
		
		return Boolean.FALSE;
	}
	
	private static Date sumMinutes(Date date, Integer minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
	
	private static void calculatePlayersStats(List<Player> players) {
		for(Player player : players) {
			player.setBetterGun(getBetterGun(player.getUsedGuns()));
			player.setStreak(getStreak(player.getKills(), player.getDeaths()));
		}
	}

	private static Integer getStreak(List<Date> kills, List<Date> deaths) {
		Integer betterStreak = 0;
		if(kills != null && !kills.isEmpty()) {
			if(deaths == null || deaths.isEmpty()) {
				return kills.size();
			} else {
				for(Date death : deaths) {
					Integer streak = 0;
					for(Date kill : kills) {
						if(kill.compareTo(death) < 0) {
							streak++;
							if(streak > betterStreak) {
								betterStreak = streak;
							}
						}
					}
				}
			}
		}
		return betterStreak;
	}

	private static Gun getBetterGun(List<Gun> usedGuns) {
		Gun gun = null;
		Integer bigTime = 0;
		if(usedGuns != null) {
			for(Gun currentGun : usedGuns) {
				if(currentGun.getTimes() > bigTime) {
					gun = currentGun;
					bigTime = currentGun.getTimes();
				}
			}
		}
		return gun;
	}

	private static void writeToOutputFile(String result) {
		File file = new File(OUTPUT_PATH);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(result);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<Match> readInputFile() {
		List<Match> matches = new ArrayList<Match>();
		try {
			FileReader file = new FileReader(LOG_PATH);
			BufferedReader reader = new BufferedReader(file);
			
			Match match = null;
			String line = reader.readLine();
			match = createNewMatch(line.substring(22), stringToDate(line.substring(0, 19)));
			line = reader.readLine();
			while(line != null) {
				Date data = stringToDate(line.substring(0, 19));
				if(line.contains("Match") && line.contains("has ended")) {
					match.setEnd(data);
					matches.add(match);
					line = reader.readLine();
					if(line != null) {
						match = createNewMatch(line.substring(22), stringToDate(line.substring(0, 19)));
						line = reader.readLine();
					}
					continue;
				}
				
				createPlayers(match, line.substring(22), data);
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matches;
	}
	
	private static void createPlayers(Match match, String substring, Date data) {
		if(substring.contains("killed")) {
			String [] array = substring.split(" ");
			
			Player playerKiller = getPlayer(match, array[0]);
			if(!array[0].equals("<WORLD>")) {
				if(playerKiller.getNickname() == null) {
					playerKiller.setNickname(array[0]);
					playerKiller.setUsedGuns(new ArrayList<Gun>());
					match.getPlayers().add(playerKiller);
				}
				Gun gun = getGun(playerKiller.getUsedGuns(), array[4]);
				if(gun.getName() == null) {
					gun.setName(array[4]);
				}
				gun.setTimes(gun.getTimes() != null ? gun.getTimes()+1 : 1);
				if(playerKiller.getUsedGuns() == null) {
					playerKiller.setUsedGuns(new ArrayList<Gun>());
				}
				playerKiller.getUsedGuns().add(gun);
				if(playerKiller.getKills() == null) {
					playerKiller.setKills(new ArrayList<Date>());
				}
				playerKiller.getKills().add(data);
			}
			Player deadPlayer = getPlayer(match, array[2]);
			if(deadPlayer.getNickname() == null) {
				deadPlayer.setNickname(array[2]);
				match.getPlayers().add(deadPlayer);
			}
			if(deadPlayer.getDeaths() == null) {
				deadPlayer.setDeaths(new ArrayList<Date>());
			}
			deadPlayer.getDeaths().add(data);
		}
	}

	private static Gun getGun(List<Gun> usedGuns, String gunName) {
		if(usedGuns != null) {
			for(Gun gun : usedGuns) {
				if(gun.getName().equals(gunName)) {
					return gun;
				}
			}
		}
		return new Gun();
	}

	private static Player getPlayer(Match match, String playerName) {
		for(Player player : match.getPlayers()) {
			if(player.getNickname().equals(playerName)) {
				return player;
			}
		}
		return new Player();
	}

	private static Match createNewMatch(String line, Date begin) {
		char[] letters = line.toCharArray();
		String matchId = "";
		for(int i=0; i < letters.length; i++) {
			if(Character.isDigit(letters[i])) {
				matchId += letters[i];
			}
		}
		
		Match match = new Match(Long.parseLong(matchId), begin, new ArrayList<Player>());
		return match;
	}

	private static Date stringToDate(String stringDate) {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date retorno = null;
		try {
			retorno = format.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retorno;
	}

}
