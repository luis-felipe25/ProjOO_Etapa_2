import java.util.Random;

public class Strategy {
    public static void main(String[] args) {
        // Gerar dados simulados
        SimuladorDeEntradas simulador = new SimuladorDeEntradas();
        ContextoRota contexto = simulador.gerarContexto();

        System.out.println("=== Simulação de Rota ===");
        System.out.println(contexto);
        System.out.println();

        // Executar todas as estratégias
        RoutePlanner planner = new RoutePlanner();

        planner.setStrategy(new FastestRouteStrategy());
        Route r1 = planner.planRoute(contexto);
        System.out.println("[Mais rápida] " + r1);

        planner.setStrategy(new ShortestRouteStrategy());
        Route r2 = planner.planRoute(contexto);
        System.out.println("[Mais curta] " + r2);

        planner.setStrategy(new CheapestRouteStrategy());
        Route r3 = planner.planRoute(contexto);
        System.out.println("[Mais econômica] " + r3);

        planner.setStrategy(new EcoFriendlyStrategy());
        Route r4 = planner.planRoute(contexto);
        System.out.println("[Mais ecológica] " + r4);
    }
}

// CONTEXTO DA ROTA
class ContextoRota {
    String origem;
    String destino;
    String transporte;
    String clima;
    String trafego;

    public ContextoRota(String origem, String destino, String transporte, String clima, String trafego) {
        this.origem = origem;
        this.destino = destino;
        this.transporte = transporte;
        this.clima = clima;
        this.trafego = trafego;
    }

    @Override
    public String toString() {
        return String.format("Origem: %s | Destino: %s | Transporte: %s | Clima: %s | Tráfego: %s",
            origem, destino, transporte, clima, trafego);
    }
}

// INTERFACE STRATEGY
interface RouteStrategy {
    Route calculateRoute(ContextoRota contexto);
}

// CLASSE ROTA
class Route {
    double tempo;
    double distancia;
    double custo;
    double emissaoCO2;

    public Route(double tempo, double distancia, double custo, double emissaoCO2) {
        this.tempo = tempo;
        this.distancia = distancia;
        this.custo = custo;
        this.emissaoCO2 = emissaoCO2;
    }

    @Override
    public String toString() {
        return String.format("Tempo: %.1f min | Distância: %.1f km | Custo: R$%.2f | CO2: %.1f g",
            tempo, distancia, custo, emissaoCO2);
    }
}

// FUNÇÕES AUXILIARES DE AJUSTE
class Util {
    public static double ajustarEmissao(String transporte, double emissaoOriginal) {
        return transporte.equals("Bicicleta") ? 0.0 : emissaoOriginal;
    }

    public static double ajustarCusto(String transporte, double custoOriginal) {
        if (transporte.equals("Bicicleta")) return 0.0;
        if (transporte.equals("Metrô")) return 5.0;
        return custoOriginal;
    }
}

// ESTRATÉGIAS CONCRETAS
class FastestRouteStrategy implements RouteStrategy {
    @Override
    public Route calculateRoute(ContextoRota c) {
        double tempo = 15.0;
        if (c.trafego.equals("Pesado")) tempo += 10;
        if (c.clima.equals("Chuvoso")) tempo += 5;

        switch (c.transporte) {
            case "Bicicleta": tempo *= 1.5; break;
            case "Ônibus": tempo *= 1.2; break;
            case "Metrô": tempo *= 0.8; break;
        }

        double custo = Util.ajustarCusto(c.transporte, 9.0);
        double co2 = Util.ajustarEmissao(c.transporte, 130.0);
        return new Route(tempo, 10.0, custo, co2);
    }
}

class ShortestRouteStrategy implements RouteStrategy {
    @Override
    public Route calculateRoute(ContextoRota c) {
        double distancia = 6.5;
        double tempo = 22.0;

        if (c.transporte.equals("Bicicleta")) tempo *= 1.4;
        else if (c.transporte.equals("Ônibus")) tempo *= 1.1;

        double custo = Util.ajustarCusto(c.transporte, 5.0);
        double co2 = Util.ajustarEmissao(c.transporte, 80.0);

        return new Route(tempo, distancia, custo, co2);
    }
}

class CheapestRouteStrategy implements RouteStrategy {
    @Override
    public Route calculateRoute(ContextoRota c) {
        double tempo = 28.0;
        double distancia = 9.0;

        if (c.transporte.equals("Bicicleta")) tempo *= 1.6;

        double custo = Util.ajustarCusto(c.transporte, 4.5);
        double co2 = Util.ajustarEmissao(c.transporte, 95.0);

        return new Route(tempo, distancia, custo, co2);
    }
}

class EcoFriendlyStrategy implements RouteStrategy {
    @Override
    public Route calculateRoute(ContextoRota c) {
        double tempo = 30.0;
        double distancia = 9.5;
        double co2 = 70.0;

        if (c.clima.equals("Ensolarado") && c.trafego.equals("Leve")) {
            co2 *= 0.7;
        }

        double custo = Util.ajustarCusto(c.transporte, 5.5);
        co2 = Util.ajustarEmissao(c.transporte, co2);

        return new Route(tempo, distancia, custo, co2);
    }
}

// PLANEJADOR DE ROTAS
class RoutePlanner {
    private RouteStrategy strategy;

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public Route planRoute(ContextoRota contexto) {
        if (strategy == null) {
            throw new IllegalStateException("Estratégia não definida.");
        }
        return strategy.calculateRoute(contexto);
    }
}

// SIMULADOR DE ENTRADAS
class SimuladorDeEntradas {
    Random rand = new Random();

    String[] origens = {"Av. Paulista", "Centro", "Pinheiros"};
    String[] destinos = {"USP", "Ibirapuera", "Sé"};
    String[] transportes = {"Carro", "Bicicleta", "Ônibus", "Metrô"};
    String[] climas = {"Ensolarado", "Chuvoso", "Nublado"};
    String[] trafegos = {"Leve", "Moderado", "Pesado"};

    public ContextoRota gerarContexto() {
        String origem = origens[rand.nextInt(origens.length)];
        String destino = destinos[rand.nextInt(destinos.length)];
        String transporte = transportes[rand.nextInt(transportes.length)];
        String clima = climas[rand.nextInt(climas.length)];
        String trafego = trafegos[rand.nextInt(trafegos.length)];
        return new ContextoRota(origem, destino, transporte, clima, trafego);
    }
}

class TransportFactory{

}