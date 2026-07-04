from scapy.all import IP, TCP, UDP, Raw
from datetime import datetime


# Maps scapy layer classes to their protocol name strings.
PROTOCOL_LAYERS = {TCP: "TCP", UDP: "UDP"}


def extract_protocol_info(packet):
    """Return (protocol, src_port, dst_port) from a scapy packet."""
    for layer, name in PROTOCOL_LAYERS.items():
        if layer in packet:
            return name, packet[layer].sport, packet[layer].dport
    return "OTHER", "-", "-"


def extract_payload_preview(packet, max_bytes=30, max_hex_chars=40):
    """Return a short hex preview of the packet payload, or a placeholder."""
    if Raw in packet:
        payload = packet[Raw].load[:max_bytes]
        return payload.hex()[:max_hex_chars] + "..."
    return "No payload"


def parse_packet(packet, packet_number):
    """Parse a scapy packet into a flat list suitable for table display.

    Returns a list:
        [packet_number, timestamp, protocol, "src_ip:src_port", "dst_ip:dst_port", payload_preview]
    or ``None`` if the packet has no IP layer.
    """
    if IP not in packet:
        return None

    timestamp = datetime.now().strftime("%H:%M:%S")
    src_ip = packet[IP].src
    dst_ip = packet[IP].dst
    protocol, src_port, dst_port = extract_protocol_info(packet)
    payload_preview = extract_payload_preview(packet)

    return [
        packet_number,
        timestamp,
        protocol,
        f"{src_ip}:{src_port}",
        f"{dst_ip}:{dst_port}",
        payload_preview,
    ]
