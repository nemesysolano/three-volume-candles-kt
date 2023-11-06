import pandas as pd
from datetime import timedelta


class Trader:

    def __init__(self, signal_provider, trade_results):
        self.prev_signal = 0  # what the previous signal was was e.g. 1 for buy , -1 for sell, 0 for hold
        self.prev_traded_price = 0  # this is the previously traded price for an exisiting position (entry price)
        self.curr_stop_loss = 0  # current stop loss
        self.curr_take_profit = 0  # current take profit
        self.signal_provider = signal_provider
        self.trade_results = trade_results
        self.time_frame = 8  # Hours

    def __call__(self):
        signal = self.signal_provider()

        while not (signal is None):
            # extract meaningful values
            now = signal.timestamp
            prev = now - timedelta(hours=self.time_frame)
            history = signal.history
            prev_close_price = history[prev]['close']
            curr_close_price = history[now]['close']
            curr_high_price = history[now]['high']
            curr_low_price = history[now]['low']

            if self.prev_signal == 1 and ((self.curr_take_profit != 0 and curr_high_price >= self.curr_take_profit) or (
                    self.curr_stop_loss != 0 and curr_low_price <= self.curr_stop_loss)):
                self.prev_signal = 0  # since the sl/tp was triggered, we reset position

            if self.prev_signal == -1 and ((self.curr_take_profit != 0 and curr_low_price <= self.curr_take_profit) or (
                    self.curr_stop_loss != 0 and curr_high_price >= self.curr_stop_loss)):
                self.prev_signal = 0  # since the sl/tp was triggered, we reset position

            print("signal=%d, prev_signal=%d" % (signal, self.prev_signal))
