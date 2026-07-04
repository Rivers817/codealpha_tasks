
from scapy.all import sniff

from utils import parse_packet, display_packet_table, display_capture_status

# Store packets for table display
packet_data = []


def packet_callback(packet):
    row = parse_packet(packet, len(packet_data) + 1)
    if row is None:
        return

    packet_data.append(row)

    display_packet_table(packet_data, clear_screen=True)
    display_capture_status(len(packet_data))


print(" Starting Network Sniffer...")
print("Press Ctrl+C to stop and see summary\n")

try:
    sniff(prn=packet_callback, store=False)
except KeyboardInterrupt:
    print("\n\n" + "=" * 80)
    print("FINAL SUMMARY")
    print("=" * 80)
    display_packet_table(packet_data)
    display_capture_status(len(packet_data), final=True)
