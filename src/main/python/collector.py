from datetime import datetime
import MetaTrader5 as mt5
import pandas as pd
import time
import datetime
import argparse
import progress 
import pytz
from timeframe_dict import timeframe_dict

if __name__ =="__main__":
    # display data on the MetaTrader 5 package
    print("MetaTrader5 package author: ",mt5.__author__)
    print("MetaTrader5 package version: ",mt5.__version__)
    parser = argparse.ArgumentParser()
    parser.add_argument('symbol') 
    parser.add_argument('timeframe', choices = tuple(timeframe_dict.keys()))
    parser.add_argument('start_date',  type=datetime.date.fromisoformat) # 'yyyy-mm-dd
    args = parser.parse_args()
    symbol = args.symbol
    start_date = args.start_date
    timeframe = timeframe_dict[args.timeframe]    
    output_file_path = "data/{}-{}.csv".format(symbol, args.timeframe)

    if not mt5.initialize():
        print("initialize() failed, error code =",mt5.last_error())
        quit()
    now = datetime.datetime.now()
    timezone = pytz.timezone("Etc/UTC")
    utc_from = datetime.datetime(start_date.year, start_date.month, start_date.day, 0, 0, 0, tzinfo=timezone)
    utc_to = datetime.datetime(now.year, now.month, now.day, 23, 59, 59, tzinfo=timezone)
    rates = mt5.copy_rates_range(symbol, timeframe, utc_from, utc_to)
    rates_frame = pd.DataFrame(rates)
    rates_frame['datetime']=pd.to_datetime(rates_frame['time'], unit='s')
    rates_frame.drop('time', axis=1, inplace= True)
    rates_frame.set_index("datetime", inplace = True)
    rates_frame.to_csv(output_file_path, encoding='utf-8')