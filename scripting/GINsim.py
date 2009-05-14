######################################################################
# python helper to access some parts of GINsim API through jython
# import this file for use in your own scripts
######################################################################
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph as rg

# for the simulation
from fr.univmrs.tagc.GINsim.reg2dyn import Simulation
from fr.univmrs.tagc.GINsim.reg2dyn import DynamicalHierarchicalSimulation
from fr.univmrs.tagc.GINsim.reg2dyn import SimulationManager
from fr.univmrs.tagc.GINsim.reg2dyn import  BatchReg2dynFrame
from fr.univmrs.tagc.GINsim.reg2dyn import GsSimulationParametersManager
from fr.univmrs.tagc.GINsim.regulatoryGraph.initialState import GsInitialState
from fr.univmrs.tagc.GINsim.connectivity import AlgoConnectivity

# initial states
from fr.univmrs.tagc.GINsim.regulatoryGraph.initialState import GsInitialStateManager

# mutants
from fr.univmrs.tagc.GINsim.regulatoryGraph import GsMutantListManager

# stable states
from fr.univmrs.tagc.GINsim.stableStates import GsSearchStableStates

# for the reduction
from fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier import ModelSimplifier
from fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier import ModelSimplifierPlugin
# for reports
from fr.univmrs.tagc.common.document import OOoDocumentWriter, WikiDocumentWriter, XHTMLDocumentWriter, LaTeXDocumentWriter
from fr.univmrs.tagc.GINsim.export.generic import GsSVGExport

import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor as GD
from java.io import File
from java.util import HashMap

# load GINsim core, otherwise nothing works!
from fr.univmrs.tagc.GINsim.global import GsMain
from fr.univmrs.tagc.common import Tools
GsMain.loadCore()
Tools.HASGUI = False

def open(path):
    "open a model from a path"
    return GD.getInstance().open(File(path))

def export_SVG(graph, filename):
    "export the graph as SVG"
    GsSVGExport.exportSVG(graph, False, filename)

def reduce(graph, reduction):
    "reduce an opened model"
    # get the reduction def
    config = graph.getObject("modelSimplifier", True).getElement(reduction)
    simplifier = ModelSimplifier(graph, config, None, False)
    return simplifier.do_reduction()

def get_scc_graph(graph):
    algo = AlgoConnectivity()
    algo.configure(graph,None, AlgoConnectivity.MODE_FULL)
    return algo.compute()

def get_simulations(graph, param=None):
    "get all simulation parameters"
    simulations = graph.getObject(GsSimulationParametersManager.key, True)
    if simulations and param:
        return simulations.getElement(param)
    return simulations.v_data

def get_initialStates(graph, param=None):
    "get all defined initial states"
    ilist = graph.getObject(GsInitialStateManager.key, True).getInitialStates()
    if ilist and param:
        return ilist.getElement(param)
    return ilist.v_data

def get_inputs(graph, param=None):
    "get all defined input configurations"
    inputs = graph.getObject(GsInitialStateManager.key, True).getInputConfigs()
    if inputs and param:
        return inputs.getElement(param)
    return inputs.v_data

def get_mutants(graph, param=None):
    "get all mutants or a mutant by name"
    mutants = graph.getObject(GsMutantListManager.key, True)
    if param:
        return mutants.getElement(param)
    return mutants.v_data

def get_stable_states(graph, mutant=None):
    stable_algo = GsSearchStableStates(graph, mutant, None)
    return stable_algo.getStable()

def get_states_from_mdd(nbNodes, mdd):
    path = [ -1 for i in xrange(nbNodes) ]
    states = []
    browse_mdd(path, mdd, states)
    return states

def browse_mdd(path, mdd, states):
    if mdd.next is None:
        if mdd.value > 0:
            states.append(path[:])
    else:
        for i in xrange(len(mdd.next)):
            path[mdd.level] = i
            browse_mdd(path, mdd.next[i], states)
        path[mdd.level] = -1

class SimulationReporter(SimulationManager):
    "helper to perform simulations"
    def __init__(self, graph, stable_handler=None):
        self.graph = graph
        self.nodeOrder = graph.getNodeOrder()
        self.stable_handler = stable_handler

    def endSimu(self, graph):
        print graph

    def setProgress(self, n):
        pass

    def addStableState(self, item):
        if self.stable_handler is not None:
            self.stable_handler(item)

    def run(self, simulation, init=None, inputs=None):
        if simulation.__class__ == "".__class__:
            simulation = get_simulations(self.graph, simulation)
        if simulation.buildSTG == 2:    # FIXME: hacky
            sim = DynamicalHierarchicalSimulation(self.graph, self, simulation, False, True)
        else:
            sim = Simulation(self.graph, self, simulation, False, True)

        if init or inputs:
            m_init, m_input = self.get_mstate(init), self.get_mstate(inputs)
            sim.set_initialStates(self.nodeOrder, m_input, m_init);
        return sim.do_simulation()

    def get_mstate(self, state):
        try:
            istate = GsInitialState()
            istate.setState(state, self.nodeOrder)
        except:
            istate = state
        m_state = HashMap()
        m_state[istate] = None
        return m_state


def create_report(path, properties={}):
    "create a report file, this creates the file and DocumentWriter, don't forget to close it"
    if path.endswith("html"):
        dw = XHTMLDocumentWriter()
    elif path.endswith("odt"):
        dw = OOoDocumentWriter()
    elif path.endswith("tex"):
        dw = LaTeXDocumentWriter()
    else:
        dw = WikiDocumentWriter()
    dw.setOutput(File(path))
    for prop, value in properties.items():
        dw.setDocumentProperty(prop, value)
    dw.startDocument()
    return dw

def name_state(state, graph):
    "get the name of a state (according to initial state names)"
    name = BatchReg2dynFrame.nameState(state, graph)
    return name

if __name__ == "__main__":
    # quick tests / examples
    
    graph = open("graph2.zginml")
    sr = SimulationReporter(graph)
    sr.run("parameter_1", init=[0,0,0,0,0,0])
    
    print "done"

