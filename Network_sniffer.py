
from scapy.all import sniff, IP, TCP, UDP, Raw
from tabulate import tabulate
import time
from datetime import datetime

# Store packets for table display
packet_data = []

def packet_callback(packet):
    timestamp = datetime.now().strftime("%H:%M:%S")
    src_ip = ""
    dst_ip = ""
    protocol = ""
    src_port = ""
    dst_port = ""
    payload_preview = ""
    
    if IP in packet:
        src_ip = packet[IP].src
        dst_ip = packet[IP].dst
        
        if TCP in packet:
            protocol = "TCP"
            src_port = packet[TCP].sport
            dst_port = packet[TCP].dport
        elif UDP in packet:
            protocol = "UDP"
            src_port = packet[UDP].sport
            dst_port = packet[UDP].dport
        else:
            protocol = "OTHER"
            src_port = "-"
            dst_port = "-"
            
        if Raw in packet:
            payload = packet[Raw].load[:30]
            payload_preview = payload.hex()[:40] + "..."
        else:
            payload_preview = "No payload"
            
        # Add to list
        packet_data.append([
            len(packet_data) + 1,
            timestamp,
            protocol,
            f"{src_ip}:{src_port}",
            f"{dst_ip}:{dst_port}",
            payload_preview
        ])
        
        # Clear screen and display table (optional - works on most terminals)
        print("\033c", end="")  # Clear screen
        
        # Display table
        headers = ["#", "Time", "Protocol", "Source", "Destination", "Payload Preview"]
        print(tabulate(packet_data, headers=headers, tablefmt="grid"))
        print(f"\n Captured {len(packet_data)} packets (Ctrl+C to stop)")

print(" Starting Network Sniffer...")
print("Press Ctrl+C to stop and see summary\n")

try:
    sniff(prn=packet_callback, store=False)
except KeyboardInterrupt:
    print("\n\n" + "="*80)
    print("FINAL SUMMARY")
    print("="*80)
    print(tabulate(packet_data, headers=["#", "Time", "Protocol", "Source", "Destination", "Payload Preview"], tablefmt="grid"))
    print(f"\n Total packets captured: {len(packet_data)}")
