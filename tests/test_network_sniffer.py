"""Unit tests for Network_sniffer.py.

The sniffer's core logic lives in ``packet_callback``, which parses a scapy
packet and appends a display row to the module-level ``packet_data`` list.
These tests build real scapy packets and exercise every protocol branch, plus
the ``main`` entry point (with ``sniff`` mocked so no live capture happens).
"""

import re
from datetime import datetime
from unittest import mock

import pytest
from scapy.all import ARP, ICMP, IP, TCP, UDP, Ether, Raw

import Network_sniffer as ns


@pytest.fixture(autouse=True)
def clear_packet_data():
    """Reset the shared module state before and after each test."""
    ns.packet_data.clear()
    yield
    ns.packet_data.clear()


def test_tcp_packet_with_payload():
    pkt = IP(src="10.0.0.1", dst="10.0.0.2") / TCP(sport=1234, dport=80) / Raw(load=b"hello world")
    ns.packet_callback(pkt)

    assert len(ns.packet_data) == 1
    idx, timestamp, protocol, source, dest, payload = ns.packet_data[0]
    assert idx == 1
    assert protocol == "TCP"
    assert source == "10.0.0.1:1234"
    assert dest == "10.0.0.2:80"
    # Payload preview is the hex of the first 30 bytes, truncated to 40 chars + "..."
    assert payload == b"hello world".hex()[:40] + "..."
    assert re.fullmatch(r"\d{2}:\d{2}:\d{2}", timestamp)


def test_udp_packet_without_payload():
    pkt = IP(src="192.168.1.5", dst="8.8.8.8") / UDP(sport=53, dport=5353)
    ns.packet_callback(pkt)

    idx, _, protocol, source, dest, payload = ns.packet_data[0]
    assert protocol == "UDP"
    assert source == "192.168.1.5:53"
    assert dest == "8.8.8.8:5353"
    assert payload == "No payload"


def test_other_ip_protocol_uses_dash_ports():
    pkt = IP(src="1.1.1.1", dst="2.2.2.2") / ICMP()
    ns.packet_callback(pkt)

    idx, _, protocol, source, dest, payload = ns.packet_data[0]
    assert protocol == "OTHER"
    assert source == "1.1.1.1:-"
    assert dest == "2.2.2.2:-"
    assert payload == "No payload"


def test_non_ip_packet_is_ignored():
    pkt = Ether() / ARP()
    ns.packet_callback(pkt)
    assert ns.packet_data == []


def test_long_payload_is_truncated_to_30_bytes():
    raw = bytes(range(60))  # 60 bytes; only first 30 should be considered
    pkt = IP(src="10.0.0.1", dst="10.0.0.2") / TCP(sport=1, dport=2) / Raw(load=raw)
    ns.packet_callback(pkt)

    payload = ns.packet_data[0][5]
    expected = raw[:30].hex()[:40] + "..."
    assert payload == expected


def test_row_indices_increment_across_packets():
    for _ in range(3):
        ns.packet_callback(IP(src="10.0.0.1", dst="10.0.0.2") / TCP(sport=1, dport=2))

    assert [row[0] for row in ns.packet_data] == [1, 2, 3]


def test_timestamp_matches_current_time():
    fixed = datetime(2020, 1, 2, 3, 4, 5)
    with mock.patch.object(ns, "datetime") as mock_dt:
        mock_dt.now.return_value = fixed
        ns.packet_callback(IP(src="10.0.0.1", dst="10.0.0.2") / UDP(sport=1, dport=2))

    assert ns.packet_data[0][1] == "03:04:05"


def test_main_invokes_sniff():
    with mock.patch.object(ns, "sniff") as mock_sniff:
        ns.main()
    mock_sniff.assert_called_once_with(prn=ns.packet_callback, store=False)


def test_main_handles_keyboard_interrupt():
    with mock.patch.object(ns, "sniff", side_effect=KeyboardInterrupt):
        # Should print the summary and swallow the interrupt without raising.
        ns.main()
