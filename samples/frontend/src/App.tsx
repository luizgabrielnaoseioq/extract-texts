const menuItems = [
  { label: "Dashboard", description: "View your latest metrics" },
  { label: "Reports", description: "Analyze customer data" }
];

export function App() {
  return (
    <main className="flex min-h-screen flex-col gap-4">
      <PageHeader title="Customer Portal" description="Manage your customers and reports" />
      <h1>Welcome back</h1>
      <p>Review recent activity before exporting data.</p>
      <input placeholder="Search customers" aria-label="Customer search" />
      <button title="Save current filters">Save changes</button>
    </main>
  );
}
