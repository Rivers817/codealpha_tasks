from tabulate import tabulate


PACKET_TABLE_HEADERS = ["#", "Time", "Protocol", "Source", "Destination", "Payload Preview"]


def display_packet_table(packet_data, clear_screen=False):
    """Print ``packet_data`` rows as a formatted grid table.

    When *clear_screen* is True the terminal is cleared first so the table
    redraws in place (useful for a live-updating display).
    """
    if clear_screen:
        print("\033c", end="")
    print(tabulate(packet_data, headers=PACKET_TABLE_HEADERS, tablefmt="grid"))


def display_capture_status(count, final=False):
    """Print a status line showing how many packets have been captured.

    Set *final* to True for the end-of-capture summary line.
    """
    if final:
        print(f"\n Total packets captured: {count}")
    else:
        print(f"\n Captured {count} packets (Ctrl+C to stop)")
