package browngu.logging;

import java.util.*;

/**
 * Service provider for {@link System.LoggerFinder} that forwards calls to {@link Logger#logger()}
 */
public class LoggerFinder extends System.LoggerFinder {
	private final System.Logger systemLogger = new System.Logger() {
		final Map<Level, Logger.Level> levelCache = new HashMap<>();

		@Override
		public String getName() {
			return Logger.class.getName();
		}

		@Override
		public boolean isLoggable(System.Logger.Level level) {
			return true;
		}

		@Override
		public void log(System.Logger.Level level, ResourceBundle bundle, String msg, Throwable thrown) {
			Logger.logger().log(translateLevel(level), thrown, msg);
		}

		@Override
		public void log(Level level, Object obj) {
			if (obj instanceof Throwable t) {
				Logger.logger().log(translateLevel(level), t);
			} else {
				System.Logger.super.log(level, obj);
			}
		}

		@Override
		public void log(System.Logger.Level level, ResourceBundle bundle, String format, Object... params) {
			Logger.logger().log(translateLevel(level), format, params);
		}

		Logger.Level translateLevel(System.Logger.Level level) {
			return levelCache.computeIfAbsent(level, this::createLevel);
		}

		Logger.Level createLevel(Level l) {
			return new Logger.Level() {
				@Override
				public int level() {
					return l.getSeverity();
				}

				@Override
				public String toString() {
					return l.toString();
				}
			};
		}
	};

	@Override
	public System.Logger getLogger(String name, Module module) {
		return systemLogger;
	}
}
