import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Fishron {
    private String name;
    private List<Task> storage = new ArrayList<>();

    public Fishron(String name) {
        this.name = name;
    }

    public void greet() {
        System.out.println("Hello! I'm " + name);
        System.out.println("What can I do for you?");
    }

    public void farewell() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public void addTask(Task task) {
        storage.add(task);
        System.out.println("____________________________________________________________");
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task.toString());
        System.out.println("Now you have " + storage.size() + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    public void listTasks() {
        System.out.println("____________________________________________________________");
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < storage.size(); i++) {
            Task task = storage.get(i);
            int count = i + 1;
            System.out.println(count + "." + task.toString());
        }
        System.out.println("____________________________________________________________");
    }

    public void markTaskAsDone(int Index) {
        if (Index >= 1 && Index <= storage.size()) {
            Task task = storage.get(Index - 1);
            task.markAsDone();
            System.out.println("____________________________________________________________");
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + task.toString());
            System.out.println("____________________________________________________________");
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public void unmarkTask(int Index) {
        if (Index >= 1 && Index <= storage.size()) {
            Task task = storage.get(Index - 1);
            task.markAsUndone();
            System.out.println("____________________________________________________________");
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + task.toString());
            System.out.println("____________________________________________________________");
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public void deleteTask(int index) {
        if (index >= 1 && index <= storage.size()) {
            Task removedTask = storage.remove(index - 1);
            System.out.println("____________________________________________________________");
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask);
            System.out.println("Now you have " + storage.size() + " tasks in the list.");
            System.out.println("____________________________________________________________");
        } else {
            System.out.println("Invalid task number.");
        }
    }

    public static void main(String[] args) {
        String botName = "Fishron";
        Fishron chatBot = new Fishron(botName);

        chatBot.greet();
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            input = scanner.nextLine();
            try {
            if (input.toLowerCase().startsWith("todo")) {
                String[] parts = input.split("todo");
                if (parts.length < 2) {
                    throw new FishronException("☹ OOPS!!! The description of a todo cannot be empty.");
                }
                chatBot.addTask(new ToDo(parts[1]));
            } else if (input.toLowerCase().startsWith("deadline")) {
                String[] parts = input.split("/by");
                if (parts.length != 2 || parts[1].isEmpty()) {
                    throw new FishronException("☹ OOPS!!! Please provide a valid deadline format.");
                }
                chatBot.addTask(new Deadline(parts[0].split("deadline")[1], parts[1]));
            } else if (input.toLowerCase().startsWith("event")) {
                String[] parts = input.split("/from|/to");
                if (parts.length != 3 || parts[1].isEmpty() || parts[2].isEmpty()) {
                    throw new FishronException("☹ OOPS!!! Please provide a valid event format.");
                }
                chatBot.addTask(new Event(parts[0].split("event")[1], parts[1], parts[2]));
            } else if (input.equalsIgnoreCase("list")) {
                chatBot.listTasks();
            } else if (input.toLowerCase().startsWith("mark")) {
                int taskIndex = Integer.parseInt(input.split(" ")[1]);
                chatBot.markTaskAsDone(taskIndex);
            } else if (input.toLowerCase().startsWith("unmark")) {
                int taskIndex = Integer.parseInt(input.split(" ")[1]);
                chatBot.unmarkTask(taskIndex);
            } else if (input.toLowerCase().startsWith("delete")) {
                int taskIndex = Integer.parseInt(input.split(" ")[1]);
                chatBot.deleteTask(taskIndex);
            } else if (!input.equalsIgnoreCase("bye")) {
                throw new FishronException("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
            }
            } catch (FishronException e) {
                System.out.println("____________________________________________________________");
                System.out.println(e.getMessage());
                System.out.println("____________________________________________________________");
            }
        } while (!input.equalsIgnoreCase("bye")) ;

            chatBot.farewell();
            System.out.println("____________________________________________________________");
    }
}
