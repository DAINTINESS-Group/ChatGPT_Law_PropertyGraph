import os
import xml.etree.ElementTree as ET
import copy

# Constants for namespaces
GRAPHML_NS = "http://graphml.graphdrawing.org/xmlns"
YFILES_NS  = "http://www.yworks.com/xml/graphml"

# Paths
RESOURCES_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUTPUT_DIR  = os.path.join(RESOURCES_DIR, 'output')
INPUT_DIR   = os.path.join(OUTPUT_DIR, 'graphML')
OUTPUT_FILE = os.path.join(OUTPUT_DIR, 'merged.graphML')

# 1) Prepare root <graphML> with namespace declarations
graphml = ET.Element('graphML', attrib={
    'xmlns':    GRAPHML_NS,
    'xmlns:y':  YFILES_NS
})

# 2) Define the standard keys
ET.SubElement(graphml, 'key', attrib={
    'id':        "n0",
    'for':       "node",
    'attr.name': "nodeLabel",
    'attr.type': "string"
})
ET.SubElement(graphml, 'key', attrib={
    'id':        "e0",
    'for':       "edge",
    'attr.name': "edgeLabel",
    'attr.type': "string"
})

# 3) Create the main <graph> element
graph = ET.SubElement(graphml, 'graph', id="G", edgedefault="directed")

existing_nodes = set()
existing_edges = set()

# 4) Gather and sort input files
files = sorted(
    [f for f in os.listdir(INPUT_DIR) if f.startswith('response') and f.endswith('.graphML')],
    key=lambda x: int(x.split('_')[-1].replace('.graphML', '')) if '_' in x else 0
)

# 5) First pass – merge nodes
for filename in files:
    path = os.path.join(INPUT_DIR, filename)
    try:
        tree = ET.parse(path)
        root = tree.getroot()
        graph_element = root.find('graph') or root

        for node in graph_element.findall('.//node'):
            node_id = node.attrib.get('id')
            if node_id not in existing_nodes:
                existing_nodes.add(node_id)
                new_node = ET.SubElement(graph, 'node', id=node_id)

                # Copy the node's label text
                data = node.find('data')
                # text = data.text.strip() if data is not None and data.text else node_id
                text = node.attrib.get('id', 'unknown')

                d = ET.SubElement(new_node, 'data', key="n0")
                shape = ET.SubElement(d, f'{{{YFILES_NS}}}ShapeNode')
                lbl   = ET.SubElement(shape, f'{{{YFILES_NS}}}NodeLabel',
                                      modelName="custom", modelPosition="center", visible="true")
                lbl.text = text

    except Exception as e:
        print(f"[ERROR] parsing nodes from {filename}: {e}")



# 6) Second pass – merge edges, preserving 'data' attribute & children
for filename in files:
    path = os.path.join(INPUT_DIR, filename)
    try:
        tree = ET.parse(path)
        root = tree.getroot()
        graph_element = root.find('graph') or root

        for edge in graph_element.findall('.//edge'):
            src = edge.attrib.get('source')
            tgt = edge.attrib.get('target')
            if src in existing_nodes and tgt in existing_nodes:
                key = (src, tgt)
                if key not in existing_edges:
                    existing_edges.add(key)

                    # Copy all original edge attributes, including your data="9.2.5"
                    edge_attribs = edge.attrib.copy()
                    new_edge = ET.SubElement(graph, 'edge', attrib=edge_attribs)

                    # Deep-copy any original <data> children
                    for data_elem in edge.findall('data'):
                        new_edge.append(copy.deepcopy(data_elem))

                    # Ensure we still add the yFiles EdgeLabel block for rendering
                    has_e0 = any(d.get('key') == 'e0' for d in new_edge.findall('data'))
                    if not has_e0:
                        d  = ET.SubElement(new_edge, 'data', key="e0")
                        pe = ET.SubElement(d, f'{{{YFILES_NS}}}PolyLineEdge')
                        lbl= ET.SubElement(pe, f'{{{YFILES_NS}}}EdgeLabel',
                                           modelName="custom", modelPosition="center", visible="true")
                        lbl.text = edge_attribs.get('label', '')


    except Exception as e:
        print(f"[ERROR] parsing edges from {filename}: {e}")

# 7) Write out the merged GraphML
tree = ET.ElementTree(graphml)
tree.write(OUTPUT_FILE, encoding="utf-8", xml_declaration=True)

print(f"[SUCCESS] Merged GraphML saved to '{OUTPUT_FILE}'")