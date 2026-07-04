import sys
import os

try:
    from scapy.all import sniff, IP, TCP, UDP, Raw
except ImportError:
    print("Error: 'scapy' is not installed. Install it with: pip install scapy")
    sys.exit(1)

try:
    from tabulate import tabulate
except ImportError:
    print("Error: 'tabulate' is not installed. Install it with: pip install tabulate")
    sys.exit(1)

from datetime import datetime

# Store packets for table display
packet_data = []
# Track packets that failed to parse
error_count = 0


def packet_callback(packet):
    """Process a captured packet and display updated table.

    Errors during packet parsing are logged and counted rather than
    crashing the sniffer, so capture continues uninterrupted.
    """
    global error_count

    try:
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
            if error_count:
                print(f" ({error_count} malformed packets skipped)")

    except Exception as e:
        error_count += 1
        print(f"\nWarning: Failed to process packet: {e}", file=sys.stderr)


def print_summary():
    """Print a final summary of all captured packets."""
    print("\n\n" + "=" * 80)
    print("FINAL SUMMARY")
    print("=" * 80)
    headers = ["#", "Time", "Protocol", "Source", "Destination", "Payload Preview"]
    print(tabulate(packet_data, headers=headers, tablefmt="grid"))
    print(f"\n Total packets captured: {len(packet_data)}")
    if error_count:
        print(f" Malformed packets skipped: {error_count}")


def main():
    # Check for root/admin privileges required by raw socket sniffing
    if os.name == "posix" and os.geteuid() != 0:
        print(
            "Error: This script requires root privileges to capture packets.\n"
            "Run with: sudo python Network_sniffer.py"
        )
        sys.exit(1)

    print(" Starting Network Sniffer...")
    print("Press Ctrl+C to stop and see summary\n")

    try:
        sniff(prn=packet_callback, store=False)
    except KeyboardInterrupt:
        print_summary()
    except PermissionError:
        print(
            "\nError: Permission denied. Raw socket access requires elevated "
            "privileges.\nRun with: sudo python Network_sniffer.py"
        )
        sys.exit(1)
    except OSError as e:
        print(f"\nError: Network interface error: {e}")
        print("Ensure a valid network interface is available and up.")
        sys.exit(1)
    except Exception as e:
        # Catch-all so the summary is still printed on unexpected errors
        print(f"\nError: Unexpected error during capture: {e}", file=sys.stderr)
        print_summary()
        sys.exit(1)


if __name__ == "__main__":
    main()
